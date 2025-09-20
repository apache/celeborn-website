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

package org.apache.celeborn.common.meta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.common.identity.UserIdentifier;
import org.apache.celeborn.common.protocol.PartitionType;
import org.apache.celeborn.common.util.Utils;

public class FileInfo {
  private static Logger logger = LoggerFactory.getLogger(FileInfo.class);
  private String mountPoint;
  private final String filePath;
  private final PartitionType partitionType;
  private final UserIdentifier userIdentifier;

  // members for ReducePartition
  private final List<Long> chunkOffsets;

  // members for MapPartition
  private int bufferSize;
  private int numSubpartitions;

  private volatile long bytesFlushed;
  // whether to split is decided by client side.
  // now it's just used for mappartition to compatible with old client which can't support split
  private boolean partitionSplitEnabled;

  public FileInfo(String filePath, List<Long> chunkOffsets, UserIdentifier userIdentifier) {
    this(filePath, chunkOffsets, userIdentifier, PartitionType.REDUCE, true);
  }

  public FileInfo(
      String filePath,
      List<Long> chunkOffsets,
      UserIdentifier userIdentifier,
      PartitionType partitionType,
      boolean partitionSplitEnabled) {
    this.filePath = filePath;
    this.chunkOffsets = chunkOffsets;
    this.userIdentifier = userIdentifier;
    this.partitionType = partitionType;
    this.partitionSplitEnabled = partitionSplitEnabled;
  }

  public FileInfo(
      String filePath,
      List<Long> chunkOffsets,
      UserIdentifier userIdentifier,
      PartitionType partitionType,
      int bufferSize,
      int numSubpartitions,
      long bytesFlushed,
      boolean partitionSplitEnabled) {
    this.filePath = filePath;
    this.chunkOffsets = chunkOffsets;
    this.userIdentifier = userIdentifier;
    this.partitionType = partitionType;
    this.bufferSize = bufferSize;
    this.numSubpartitions = numSubpartitions;
    this.bytesFlushed = bytesFlushed;
    this.partitionSplitEnabled = partitionSplitEnabled;
  }

  public FileInfo(String filePath, UserIdentifier userIdentifier, PartitionType partitionType) {
    this(filePath, new ArrayList(Arrays.asList(0L)), userIdentifier, partitionType, true);
  }

  public FileInfo(
      String filePath,
      UserIdentifier userIdentifier,
      PartitionType partitionType,
      boolean partitionSplitEnabled) {
    this(
        filePath,
        new ArrayList(Arrays.asList(0L)),
        userIdentifier,
        partitionType,
        partitionSplitEnabled);
  }

  @VisibleForTesting
  public FileInfo(File file, UserIdentifier userIdentifier) {
    this(
        file.getAbsolutePath(),
        new ArrayList(Arrays.asList(0L)),
        userIdentifier,
        PartitionType.REDUCE,
        true);
  }

  public synchronized void addChunkOffset(long bytesFlushed) {
    chunkOffsets.add(bytesFlushed);
  }

  public synchronized int numChunks() {
    if (!chunkOffsets.isEmpty()) {
      return chunkOffsets.size() - 1;
    } else {
      return 0;
    }
  }

  public synchronized long getLastChunkOffset() {
    return chunkOffsets.get(chunkOffsets.size() - 1);
  }

  public long getFileLength() {
    return bytesFlushed;
  }

  public long updateBytesFlushed(int numBytes) {
    bytesFlushed += numBytes;
    return bytesFlushed;
  }

  public File getFile() {
    return new File(filePath);
  }

  public String getFilePath() {
    return filePath;
  }

  public String getSortedPath() {
    return Utils.getSortedFilePath(filePath);
  }

  public String getIndexPath() {
    return Utils.getIndexFilePath(filePath);
  }

  public Path getHdfsPath() {
    return new Path(filePath);
  }

  public Path getHdfsIndexPath() {
    return new Path(Utils.getIndexFilePath(filePath));
  }

  public Path getHdfsSortedPath() {
    return new Path(Utils.getSortedFilePath(filePath));
  }

  public Path getHdfsWriterSuccessPath() {
    return new Path(Utils.getWriteSuccessFilePath(filePath));
  }

  public Path getHdfsPeerWriterSuccessPath() {
    return new Path(Utils.getWriteSuccessFilePath(Utils.getPeerPath(filePath)));
  }

  public UserIdentifier getUserIdentifier() {
    return userIdentifier;
  }

  public void deleteAllFiles(FileSystem hdfsFs) {
    if (isHdfs()) {
      try {
        hdfsFs.delete(getHdfsPath(), false);
        hdfsFs.delete(getHdfsWriterSuccessPath(), false);
        hdfsFs.delete(getHdfsIndexPath(), false);
        hdfsFs.delete(getHdfsSortedPath(), false);
      } catch (Exception e) {
        // ignore delete exceptions because some other workers might be deleting the directory
        logger.debug(
            "delete HDFS file {},{},{},{} failed {}",
            getHdfsPath(),
            getHdfsWriterSuccessPath(),
            getHdfsIndexPath(),
            getHdfsSortedPath(),
            e);
      }
    } else {
      getFile().delete();
      new File(getIndexPath()).delete();
      new File(getSortedPath()).delete();
    }
  }

  public boolean isHdfs() {
    return Utils.isHdfsPath(filePath);
  }

  public synchronized List<Long> getChunkOffsets() {
    return chunkOffsets;
  }

  public PartitionType getPartitionType() {
    return partitionType;
  }

  @Override
  public String toString() {
    return "FileInfo{"
        + "file="
        + filePath
        + ", chunkOffsets="
        + StringUtils.join(this.chunkOffsets, ",")
        + ", userIdentifier="
        + userIdentifier.toString()
        + ", partitionType="
        + partitionType
        + '}';
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public int getNumSubpartitions() {
    return numSubpartitions;
  }

  public void setNumSubpartitions(int numSubpartitions) {
    this.numSubpartitions = numSubpartitions;
  }

  public String getMountPoint() {
    return mountPoint;
  }

  public void setMountPoint(String mountPoint) {
    this.mountPoint = mountPoint;
  }

  public long getBytesFlushed() {
    return bytesFlushed;
  }

  public boolean isPartitionSplitEnabled() {
    return partitionSplitEnabled;
  }

  public void setPartitionSplitEnabled(boolean partitionSplitEnabled) {
    this.partitionSplitEnabled = partitionSplitEnabled;
  }
}
