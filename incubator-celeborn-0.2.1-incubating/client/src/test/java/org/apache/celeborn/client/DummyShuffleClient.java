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

package org.apache.celeborn.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.client.read.RssInputStream;
import org.apache.celeborn.common.protocol.PartitionLocation;
import org.apache.celeborn.common.rpc.RpcEndpointRef;

public class DummyShuffleClient extends ShuffleClient {

  private static final Logger LOG = LoggerFactory.getLogger(DummyShuffleClient.class);

  private final OutputStream os;

  public DummyShuffleClient(File file) throws Exception {
    this.os = new BufferedOutputStream(new FileOutputStream(file));
  }

  @Override
  public void setupMetaServiceRef(String host, int port) {}

  @Override
  public void setupMetaServiceRef(RpcEndpointRef endpointRef) {}

  @Override
  public int pushData(
      String applicationId,
      int shuffleId,
      int mapId,
      int attemptId,
      int partitionId,
      byte[] data,
      int offset,
      int length,
      int numMappers,
      int numPartitions)
      throws IOException {
    os.write(data, offset, length);
    return length;
  }

  @Override
  public void prepareForMergeData(int shuffleId, int mapId, int attemptId) throws IOException {}

  @Override
  public int mergeData(
      String applicationId,
      int shuffleId,
      int mapId,
      int attemptId,
      int partitionId,
      byte[] data,
      int offset,
      int length,
      int numMappers,
      int numPartitions)
      throws IOException {
    os.write(data, offset, length);
    return length;
  }

  @Override
  public void pushMergedData(String applicationId, int shuffleId, int mapId, int attemptId) {}

  @Override
  public void mapperEnd(
      String applicationId, int shuffleId, int mapId, int attemptId, int numMappers) {}

  @Override
  public void cleanup(String applicationId, int shuffleId, int mapId, int attemptId) {}

  @Override
  public RssInputStream readPartition(
      String applicationId,
      int shuffleId,
      int partitionId,
      int attemptNumber,
      int startMapIndex,
      int endMapIndex) {
    return null;
  }

  @Override
  public RssInputStream readPartition(
      String applicationId, int shuffleId, int partitionId, int attemptNumber) {
    return null;
  }

  @Override
  public boolean unregisterShuffle(String applicationId, int shuffleId, boolean isDriver) {
    return false;
  }

  @Override
  public void shutdown() {
    try {
      os.close();
    } catch (IOException e) {
      LOG.error("Closing file failed.", e);
    }
  }

  @Override
  public Optional<PartitionLocation> regionStart(
      String applicationId,
      int shuffleId,
      int mapId,
      int attemptId,
      PartitionLocation location,
      int currentRegionIdx,
      boolean isBroadcast)
      throws IOException {
    return Optional.empty();
  }

  @Override
  public void regionFinish(
      String applicationId, int shuffleId, int mapId, int attemptId, PartitionLocation location)
      throws IOException {}

  @Override
  public void pushDataHandShake(
      String applicationId,
      int shuffleId,
      int mapId,
      int attemptId,
      int numPartitions,
      int bufferSize,
      PartitionLocation location)
      throws IOException {}
}
