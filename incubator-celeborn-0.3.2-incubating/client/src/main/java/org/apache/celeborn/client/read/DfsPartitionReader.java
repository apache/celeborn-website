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

package org.apache.celeborn.client.read;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.client.ShuffleClient;
import org.apache.celeborn.common.CelebornConf;
import org.apache.celeborn.common.network.client.TransportClient;
import org.apache.celeborn.common.network.client.TransportClientFactory;
import org.apache.celeborn.common.network.protocol.TransportMessage;
import org.apache.celeborn.common.protocol.MessageType;
import org.apache.celeborn.common.protocol.PartitionLocation;
import org.apache.celeborn.common.protocol.PbOpenStream;
import org.apache.celeborn.common.util.ShuffleBlockInfoUtils;
import org.apache.celeborn.common.util.ThreadExceptionHandler;
import org.apache.celeborn.common.util.Utils;

public class DfsPartitionReader implements PartitionReader {
  private static Logger logger = LoggerFactory.getLogger(DfsPartitionReader.class);
  PartitionLocation location;
  private final long shuffleChunkSize;
  private final int fetchMaxReqsInFlight;
  private final LinkedBlockingQueue<ByteBuf> results;
  private final AtomicReference<IOException> exception = new AtomicReference<>();
  private volatile boolean closed = false;
  private Thread fetchThread;
  private boolean fetchThreadStarted;
  private FSDataInputStream hdfsInputStream;
  private int numChunks = 0;
  private int returnedChunks = 0;
  private int currentChunkIndex = 0;
  private MetricsCallback metricsCallback;

  public DfsPartitionReader(
      CelebornConf conf,
      String shuffleKey,
      PartitionLocation location,
      TransportClientFactory clientFactory,
      int startMapIndex,
      int endMapIndex,
      MetricsCallback metricsCallback)
      throws IOException {
    shuffleChunkSize = conf.dfsReadChunkSize();
    fetchMaxReqsInFlight = conf.clientFetchMaxReqsInFlight();
    results = new LinkedBlockingQueue<>();

    this.metricsCallback = metricsCallback;
    this.location = location;

    final List<Long> chunkOffsets = new ArrayList<>();
    if (endMapIndex != Integer.MAX_VALUE) {
      long fetchTimeoutMs = conf.clientFetchTimeoutMs();
      try {
        TransportClient client =
            clientFactory.createClient(location.getHost(), location.getFetchPort());
        TransportMessage openStream =
            new TransportMessage(
                MessageType.OPEN_STREAM,
                PbOpenStream.newBuilder()
                    .setShuffleKey(shuffleKey)
                    .setFileName(location.getFileName())
                    .setStartIndex(startMapIndex)
                    .setEndIndex(endMapIndex)
                    .build()
                    .toByteArray());
        ByteBuffer response = client.sendRpcSync(openStream.toByteBuffer(), fetchTimeoutMs);
        TransportMessage.fromByteBuffer(response).getParsedPayload();
        // Parse this message to ensure sort is done.
      } catch (IOException | InterruptedException e) {
        throw new IOException(
            "read shuffle file from HDFS failed, filePath: "
                + location.getStorageInfo().getFilePath(),
            e);
      }
      hdfsInputStream =
          ShuffleClient.getHdfsFs(conf)
              .open(new Path(Utils.getSortedFilePath(location.getStorageInfo().getFilePath())));
      chunkOffsets.addAll(
          getChunkOffsetsFromSortedIndex(conf, location, startMapIndex, endMapIndex));
    } else {
      hdfsInputStream =
          ShuffleClient.getHdfsFs(conf).open(new Path(location.getStorageInfo().getFilePath()));
      chunkOffsets.addAll(getChunkOffsetsFromUnsortedIndex(conf, location));
    }
    logger.debug(
        "DFS {} index count:{} offsets:{}",
        location.getStorageInfo().getFilePath(),
        chunkOffsets.size(),
        chunkOffsets);
    if (chunkOffsets.size() > 1) {
      numChunks = chunkOffsets.size() - 1;
      fetchThread =
          new Thread(
              () -> {
                try {
                  while (!closed && currentChunkIndex < numChunks) {
                    while (results.size() >= fetchMaxReqsInFlight) {
                      Thread.sleep(50);
                    }
                    long offset = chunkOffsets.get(currentChunkIndex);
                    long length = chunkOffsets.get(currentChunkIndex + 1) - offset;
                    logger.debug("read {} offset {} length {}", currentChunkIndex, offset, length);
                    byte[] buffer = new byte[(int) length];
                    try {
                      hdfsInputStream.readFully(offset, buffer);
                    } catch (IOException e) {
                      logger.warn(
                          "read HDFS {} failed will retry, error detail {}",
                          location.getStorageInfo().getFilePath(),
                          e);
                      try {
                        hdfsInputStream.close();
                        hdfsInputStream =
                            ShuffleClient.getHdfsFs(conf)
                                .open(
                                    new Path(
                                        Utils.getSortedFilePath(
                                            location.getStorageInfo().getFilePath())));
                        hdfsInputStream.readFully(offset, buffer);
                      } catch (IOException ex) {
                        logger.warn(
                            "retry read HDFS {} failed, error detail {} ",
                            location.getStorageInfo().getFilePath(),
                            e);
                        exception.set(ex);
                        break;
                      }
                    }
                    results.put(Unpooled.wrappedBuffer(buffer));
                    logger.debug("add index {} to results", currentChunkIndex++);
                  }
                } catch (Exception e) {
                  logger.warn("Fetch thread is cancelled.", e);
                  // cancel a task for speculative, ignore this exception
                }
                logger.debug("fetch {} is done.", location.getStorageInfo().getFilePath());
              },
              "Dfs-fetch-thread" + location.getStorageInfo().getFilePath());
      fetchThread.setUncaughtExceptionHandler(new ThreadExceptionHandler(fetchThread.getName()));
      logger.debug("Start dfs read on location {}", location);
      ShuffleClient.incrementTotalReadCounter();
    }
  }

  private List<Long> getChunkOffsetsFromUnsortedIndex(CelebornConf conf, PartitionLocation location)
      throws IOException {
    FSDataInputStream indexInputStream =
        ShuffleClient.getHdfsFs(conf)
            .open(new Path(Utils.getIndexFilePath(location.getStorageInfo().getFilePath())));
    List<Long> offsets = new ArrayList<>();
    int offsetCount = indexInputStream.readInt();
    for (int i = 0; i < offsetCount; i++) {
      offsets.add(indexInputStream.readLong());
    }
    indexInputStream.close();
    return offsets;
  }

  private List<Long> getChunkOffsetsFromSortedIndex(
      CelebornConf conf, PartitionLocation location, int startMapIndex, int endMapIndex)
      throws IOException {
    String indexPath = Utils.getIndexFilePath(location.getStorageInfo().getFilePath());
    FSDataInputStream indexInputStream = ShuffleClient.getHdfsFs(conf).open(new Path(indexPath));
    logger.debug("read sorted index {}", indexPath);
    long indexSize = ShuffleClient.getHdfsFs(conf).getFileStatus(new Path(indexPath)).getLen();
    // Index size won't be large, so it's safe to do the conversion.
    byte[] indexBuffer = new byte[(int) indexSize];
    indexInputStream.readFully(0L, indexBuffer);
    List<Long> offsets =
        new ArrayList<>(
            ShuffleBlockInfoUtils.getChunkOffsetsFromShuffleBlockInfos(
                startMapIndex,
                endMapIndex,
                shuffleChunkSize,
                ShuffleBlockInfoUtils.parseShuffleBlockInfosFromByteBuffer(indexBuffer)));
    indexInputStream.close();
    return offsets;
  }

  @Override
  public boolean hasNext() {
    logger.debug("check has next current index: {} chunks {}", returnedChunks, numChunks);
    return returnedChunks < numChunks;
  }

  @Override
  public ByteBuf next() throws IOException, InterruptedException {
    ByteBuf chunk = null;
    if (!fetchThreadStarted) {
      fetchThreadStarted = true;
      fetchThread.start();
    }
    try {
      while (chunk == null) {
        checkException();
        Long startFetchWait = System.nanoTime();
        chunk = results.poll(500, TimeUnit.MILLISECONDS);
        metricsCallback.incReadTime(
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startFetchWait));
        logger.debug("poll result with result size: {}", results.size());
      }
    } catch (InterruptedException e) {
      logger.error("PartitionReader thread interrupted while fetching data.");
      throw e;
    }
    returnedChunks++;
    return chunk;
  }

  private void checkException() throws IOException {
    IOException e = exception.get();
    if (e != null) {
      throw e;
    }
  }

  @Override
  public void close() {
    closed = true;
    if (fetchThread != null) {
      fetchThread.interrupt();
    }
    try {
      hdfsInputStream.close();
    } catch (IOException e) {
      logger.warn("close HDFS input stream failed.", e);
    }
    if (results.size() > 0) {
      results.forEach(ReferenceCounted::release);
    }
    results.clear();
  }

  @Override
  public PartitionLocation getLocation() {
    return location;
  }
}
