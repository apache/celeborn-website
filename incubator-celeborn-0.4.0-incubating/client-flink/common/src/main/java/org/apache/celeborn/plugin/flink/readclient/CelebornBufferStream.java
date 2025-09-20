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

package org.apache.celeborn.plugin.flink.readclient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.flink.shaded.netty4.io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.common.network.client.RpcResponseCallback;
import org.apache.celeborn.common.network.client.TransportClient;
import org.apache.celeborn.common.network.protocol.RequestMessage;
import org.apache.celeborn.common.network.protocol.TransportMessage;
import org.apache.celeborn.common.network.protocol.TransportableError;
import org.apache.celeborn.common.network.util.NettyUtils;
import org.apache.celeborn.common.protocol.MessageType;
import org.apache.celeborn.common.protocol.PartitionLocation;
import org.apache.celeborn.common.protocol.PbBufferStreamEnd;
import org.apache.celeborn.common.protocol.PbOpenStream;
import org.apache.celeborn.common.protocol.PbReadAddCredit;
import org.apache.celeborn.common.protocol.PbStreamHandler;
import org.apache.celeborn.common.protocol.StreamType;
import org.apache.celeborn.plugin.flink.network.FlinkTransportClientFactory;

public class CelebornBufferStream {
  private static Logger logger = LoggerFactory.getLogger(CelebornBufferStream.class);
  private FlinkTransportClientFactory clientFactory;
  private String shuffleKey;
  private PartitionLocation[] locations;
  private int subIndexStart;
  private int subIndexEnd;
  private TransportClient client;
  private AtomicInteger currentLocationIndex = new AtomicInteger(0);
  private long streamId = 0;
  private FlinkShuffleClientImpl mapShuffleClient;
  private boolean isClosed;
  private boolean isOpenSuccess;
  private Object lock = new Object();
  private Supplier<ByteBuf> bufferSupplier;
  private int initialCredit;
  private Consumer<RequestMessage> messageConsumer;

  public CelebornBufferStream() {}

  public CelebornBufferStream(
      FlinkShuffleClientImpl mapShuffleClient,
      FlinkTransportClientFactory dataClientFactory,
      String shuffleKey,
      PartitionLocation[] locations,
      int subIndexStart,
      int subIndexEnd) {
    this.mapShuffleClient = mapShuffleClient;
    this.clientFactory = dataClientFactory;
    this.shuffleKey = shuffleKey;
    this.locations = locations;
    this.subIndexStart = subIndexStart;
    this.subIndexEnd = subIndexEnd;
  }

  public void open(
      Supplier<ByteBuf> bufferSupplier,
      int initialCredit,
      Consumer<RequestMessage> messageConsumer) {
    this.bufferSupplier = bufferSupplier;
    this.initialCredit = initialCredit;
    this.messageConsumer = messageConsumer;
    moveToNextPartitionIfPossible(0);
  }

  public void addCredit(PbReadAddCredit pbReadAddCredit) {
    this.client.sendRpc(
        new TransportMessage(MessageType.READ_ADD_CREDIT, pbReadAddCredit.toByteArray())
            .toByteBuffer(),
        new RpcResponseCallback() {

          @Override
          public void onSuccess(ByteBuffer response) {
            // Send PbReadAddCredit do not expect response.
          }

          @Override
          public void onFailure(Throwable e) {
            logger.warn(
                "Send PbReadAddCredit to {} failed, detail {}",
                NettyUtils.getRemoteAddress(client.getChannel()),
                e.getCause());
          }
        });
  }

  public static CelebornBufferStream empty() {
    return EMPTY_CELEBORN_BUFFER_STREAM;
  }

  public long getStreamId() {
    return streamId;
  }

  public static CelebornBufferStream create(
      FlinkShuffleClientImpl client,
      FlinkTransportClientFactory dataClientFactory,
      String shuffleKey,
      PartitionLocation[] locations,
      int subIndexStart,
      int subIndexEnd) {
    if (locations == null || locations.length == 0) {
      return empty();
    } else {
      return new CelebornBufferStream(
          client, dataClientFactory, shuffleKey, locations, subIndexStart, subIndexEnd);
    }
  }

  private static final CelebornBufferStream EMPTY_CELEBORN_BUFFER_STREAM =
      new CelebornBufferStream();

  private void closeStream(long streamId) {
    if (client != null && client.isActive()) {
      client.sendRpc(
          new TransportMessage(
                  MessageType.BUFFER_STREAM_END,
                  PbBufferStreamEnd.newBuilder()
                      .setStreamType(StreamType.CreditStream)
                      .setStreamId(streamId)
                      .build()
                      .toByteArray())
              .toByteBuffer());
    }
  }

  private void cleanStream(long streamId) {
    if (isOpenSuccess) {
      mapShuffleClient.getReadClientHandler().removeHandler(streamId);
      clientFactory.unregisterSupplier(streamId);
      closeStream(streamId);
      isOpenSuccess = false;
    }
  }

  public void close() {
    synchronized (lock) {
      cleanStream(streamId);
      isClosed = true;
    }
  }

  public void moveToNextPartitionIfPossible(long endedStreamId) {
    logger.debug(
        "MoveToNextPartitionIfPossible in this:{},  endedStreamId: {}, currentLocationIndex: {}, currentSteamId:{}, locationsLength:{}",
        this,
        endedStreamId,
        currentLocationIndex.get(),
        streamId,
        locations.length);
    if (currentLocationIndex.get() > 0) {
      logger.debug("Get end streamId {}", endedStreamId);
      cleanStream(endedStreamId);
    }
    if (currentLocationIndex.get() < locations.length) {
      try {
        openStreamInternal();
        logger.debug(
            "MoveToNextPartitionIfPossible after openStream this:{},  endedStreamId: {}, currentLocationIndex: {}, currentSteamId:{}, locationsLength:{}",
            this,
            endedStreamId,
            currentLocationIndex.get(),
            streamId,
            locations.length);
      } catch (Exception e) {
        logger.warn("Failed to open stream and report to flink framework. ", e);
        messageConsumer.accept(new TransportableError(0L, e));
      }
    }
  }

  private void openStreamInternal() throws IOException, InterruptedException {
    this.client =
        clientFactory.createClientWithRetry(
            locations[currentLocationIndex.get()].getHost(),
            locations[currentLocationIndex.get()].getFetchPort());
    String fileName = locations[currentLocationIndex.getAndIncrement()].getFileName();
    TransportMessage openStream =
        new TransportMessage(
            MessageType.OPEN_STREAM,
            PbOpenStream.newBuilder()
                .setShuffleKey(shuffleKey)
                .setFileName(fileName)
                .setStartIndex(subIndexStart)
                .setEndIndex(subIndexEnd)
                .setInitialCredit(initialCredit)
                .build()
                .toByteArray());
    client.sendRpc(
        openStream.toByteBuffer(),
        new RpcResponseCallback() {

          @Override
          public void onSuccess(ByteBuffer response) {
            try {
              PbStreamHandler pbStreamHandler =
                  TransportMessage.fromByteBuffer(response).getParsedPayload();
              CelebornBufferStream.this.streamId = pbStreamHandler.getStreamId();
              synchronized (lock) {
                if (!isClosed) {
                  clientFactory.registerSupplier(
                      CelebornBufferStream.this.streamId, bufferSupplier);
                  mapShuffleClient
                      .getReadClientHandler()
                      .registerHandler(streamId, messageConsumer, client);
                  isOpenSuccess = true;
                  logger.debug(
                      "open stream success from remote:{}, stream id:{}, fileName: {}",
                      client.getSocketAddress(),
                      streamId,
                      fileName);
                } else {
                  logger.debug(
                      "open stream success from remote:{}, but stream reader is already closed, stream id:{}, fileName: {}",
                      client.getSocketAddress(),
                      streamId,
                      fileName);
                  closeStream(streamId);
                }
              }
            } catch (Exception e) {
              logger.error(
                  "Open file {} stream for {} error from {}",
                  fileName,
                  shuffleKey,
                  NettyUtils.getRemoteAddress(client.getChannel()));
              messageConsumer.accept(new TransportableError(streamId, e));
            }
          }

          @Override
          public void onFailure(Throwable e) {
            logger.error(
                "Open file {} stream for {} error from {}",
                fileName,
                shuffleKey,
                NettyUtils.getRemoteAddress(client.getChannel()));
            messageConsumer.accept(new TransportableError(streamId, e));
          }
        });
  }

  public TransportClient getClient() {
    return client;
  }
}
