/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.celeborn.common.network.client;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.common.network.protocol.*;
import org.apache.celeborn.common.network.server.MessageHandler;
import org.apache.celeborn.common.network.util.NettyUtils;

/**
 * Handler that processes server responses, in response to requests issued from a
 * [[TransportClient]]. It works by tracking the list of outstanding requests (and their callbacks).
 *
 * <p>Concurrency: thread safe and can be called from multiple threads.
 */
public class TransportResponseHandler extends MessageHandler<ResponseMessage> {
  private static final Logger logger = LoggerFactory.getLogger(TransportResponseHandler.class);

  private final Channel channel;

  private final Map<StreamChunkSlice, ChunkReceivedCallback> outstandingFetches;

  private final Map<Long, RpcResponseCallback> outstandingRpcs;

  /** Records the time (in system nanoseconds) that the last fetch or RPC request was sent. */
  private final AtomicLong timeOfLastRequestNs;

  public TransportResponseHandler(Channel channel) {
    this.channel = channel;
    this.outstandingFetches = new ConcurrentHashMap<>();
    this.outstandingRpcs = new ConcurrentHashMap<>();
    this.timeOfLastRequestNs = new AtomicLong(0);
  }

  public void addFetchRequest(StreamChunkSlice streamChunkSlice, ChunkReceivedCallback callback) {
    updateTimeOfLastRequest();
    if (outstandingFetches.containsKey(streamChunkSlice)) {
      logger.warn("[addFetchRequest] streamChunkSlice {} already exists!", streamChunkSlice);
    }
    outstandingFetches.put(streamChunkSlice, callback);
  }

  public void removeFetchRequest(StreamChunkSlice streamChunkSlice) {
    outstandingFetches.remove(streamChunkSlice);
  }

  public void addRpcRequest(long requestId, RpcResponseCallback callback) {
    updateTimeOfLastRequest();
    if (outstandingRpcs.containsKey(requestId)) {
      logger.warn("[addRpcRequest] requestId {} already exists!", requestId);
    }
    outstandingRpcs.put(requestId, callback);
  }

  public void removeRpcRequest(long requestId) {
    outstandingRpcs.remove(requestId);
  }

  /**
   * Fire the failure callback for all outstanding requests. This is called when we have an uncaught
   * exception or pre-mature connection termination.
   */
  private void failOutstandingRequests(Throwable cause) {
    for (Map.Entry<StreamChunkSlice, ChunkReceivedCallback> entry : outstandingFetches.entrySet()) {
      try {
        entry.getValue().onFailure(entry.getKey().chunkIndex, cause);
      } catch (Exception e) {
        logger.warn("ChunkReceivedCallback.onFailure throws exception", e);
      }
    }
    for (Map.Entry<Long, RpcResponseCallback> entry : outstandingRpcs.entrySet()) {
      try {
        entry.getValue().onFailure(cause);
      } catch (Exception e) {
        logger.warn("RpcResponseCallback.onFailure throws exception", e);
      }
    }

    // It's OK if new fetches appear, as they will fail immediately.
    outstandingFetches.clear();
    outstandingRpcs.clear();
  }

  @Override
  public void channelActive() {}

  @Override
  public void channelInactive() {
    if (numOutstandingRequests() > 0) {
      String remoteAddress = NettyUtils.getRemoteAddress(channel);
      logger.error(
          "Still have {} requests outstanding when connection from {} is closed",
          numOutstandingRequests(),
          remoteAddress);
      failOutstandingRequests(new IOException("Connection from " + remoteAddress + " closed"));
    }
  }

  @Override
  public void exceptionCaught(Throwable cause) {
    if (numOutstandingRequests() > 0) {
      String remoteAddress = NettyUtils.getRemoteAddress(channel);
      logger.error(
          "Still have {} requests outstanding when connection from {} is closed",
          numOutstandingRequests(),
          remoteAddress);
      failOutstandingRequests(cause);
    }
  }

  @Override
  public void handle(ResponseMessage message) throws Exception {
    if (message instanceof ChunkFetchSuccess) {
      ChunkFetchSuccess resp = (ChunkFetchSuccess) message;
      ChunkReceivedCallback listener = outstandingFetches.get(resp.streamChunkSlice);
      if (listener == null) {
        logger.warn(
            "Ignoring response for block {} from {} since it is not outstanding",
            resp.streamChunkSlice,
            NettyUtils.getRemoteAddress(channel));
        resp.body().release();
      } else {
        outstandingFetches.remove(resp.streamChunkSlice);
        listener.onSuccess(resp.streamChunkSlice.chunkIndex, resp.body());
        resp.body().release();
      }
    } else if (message instanceof ChunkFetchFailure) {
      ChunkFetchFailure resp = (ChunkFetchFailure) message;
      ChunkReceivedCallback listener = outstandingFetches.get(resp.streamChunkSlice);
      if (listener == null) {
        logger.warn(
            "Ignoring response for block {} from {} ({}) since it is not outstanding",
            resp.streamChunkSlice,
            NettyUtils.getRemoteAddress(channel),
            resp.errorString);
      } else {
        outstandingFetches.remove(resp.streamChunkSlice);
        logger.warn("Receive ChunkFetchFailure, errorMsg {}", resp.errorString);
        listener.onFailure(
            resp.streamChunkSlice.chunkIndex,
            new ChunkFetchFailureException(
                "Failure while fetching " + resp.streamChunkSlice + ": " + resp.errorString));
      }
    } else if (message instanceof RpcResponse) {
      RpcResponse resp = (RpcResponse) message;
      RpcResponseCallback listener = outstandingRpcs.get(resp.requestId);
      if (listener == null) {
        logger.warn(
            "Ignoring response for RPC {} from {} ({} bytes) since it is not outstanding",
            resp.requestId,
            NettyUtils.getRemoteAddress(channel),
            resp.body().size());
        resp.body().release();
      } else {
        outstandingRpcs.remove(resp.requestId);
        try {
          listener.onSuccess(resp.body().nioByteBuffer());
        } finally {
          resp.body().release();
        }
      }
    } else if (message instanceof RpcFailure) {
      RpcFailure resp = (RpcFailure) message;
      RpcResponseCallback listener = outstandingRpcs.get(resp.requestId);
      if (listener == null) {
        logger.warn(
            "Ignoring response for RPC {} from {} ({}) since it is not outstanding",
            resp.requestId,
            NettyUtils.getRemoteAddress(channel),
            resp.errorString);
      } else {
        outstandingRpcs.remove(resp.requestId);
        listener.onFailure(new RuntimeException(resp.errorString));
      }
    } else {
      throw new IllegalStateException("Unknown response type: " + message.type());
    }
  }

  /** Returns total number of outstanding requests (fetch requests + rpcs) */
  public int numOutstandingRequests() {
    return outstandingFetches.size() + outstandingRpcs.size();
  }

  /** Returns the time in nanoseconds of when the last request was sent out. */
  public long getTimeOfLastRequestNs() {
    return timeOfLastRequestNs.get();
  }

  /** Updates the time of the last request to the current system time. */
  public void updateTimeOfLastRequest() {
    timeOfLastRequestNs.set(System.nanoTime());
  }
}
