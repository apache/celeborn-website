---
hide:
  - navigation

license: |
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
---

# Apache Celeborn™ 0.5.2 Release Notes

## Highlight

- Support Apache Spark barrier stages
- Support differentiate map results with only different stageAttemptId
- Fix InFlightRequestTracker should not reset totalInflightReqs for cleaning up to avoid negative totalInflightReqs for limitZeroInFlight

### Improvement

- [CELEBORN-1071] Support stage rerun for shuffle data lost
- [CELEBORN-1511] Add support for custom master endpoint resolver
- [CELEBORN-1516][FOLLOWUP] Support reset method for DynamicConfigServiceFactory
- [CELEBORN-1518] Add support for Apache Spark barrier stages
- [CELEBORN-1524] Support IPv6 hostnames for Apache Ratis
- [CELEBORN-1533] Log location when CelebornInputStream#fillBuffer fails
- [CELEBORN-1535] Support to disable master workerUnavailableInfo expiration
- [CELEBORN-1541] Enhance the readable address for internal port
- [CELEBORN-1550] Add support of providing custom dynamic store backend implementation
- [CELEBORN-1552] Automatically support prometheus to scrape metrics for helm chart
- [CELEBORN-1563] Log networkLocation in WorkerInfo
- [CELEBORN-1567] Support throw FetchFailedException when Data corruption detected
- [CELEBORN-1568] Support worker retries in MiniCluster
- [CELEBORN-1573] Change to debug logging on client side for reserve slots
- [CELEBORN-1578] Make Worker#timer have thread name and daemon
- [CELEBORN-1587] Change to debug logging on client side for SortBasedPusher trigger push
- [CELEBORN-1594] Refine dynamicConfig template and prevent NPE
- [CELEBORN-1602] Do hard split for push merged data RPC with disk full
- [CELEBORN-1615] Start the http server after all handlers added
- [CELEBORN-1625] Add parameter skipCompress for pushOrMergeData
- [CELEBORN-1638] Improve the slots allocator performance
- [CELEBORN-1643] DataPusher handle InterruptedException
- [CELEBORN-1646] Catch exception of Files#getFileStore for DeviceMonitor and StorageManager for input/ouput error
- [CELEBORN-1652] Throw TransportableError for failure of sending PbReadAddCredit to avoid flink task get stuck
- [CELEBORN-1661] Make sure that the sortedFilesDb is initialized successfully when worker enable graceful shutdown
- [CELEBORN-1663] Only register appShuffleDeterminate if stage using celeborn for shuffle
- [CELEBORN-1671] CelebornShuffleReader will try replica if create client failed
- [CELEBORN-1673] Support retry create client

### Stability and Bug Fix

- [CELEBORN-1297][FOLLOWUP] Fix DB config service SQL file
- [CELEBORN-1473] TransportClientFactory should register netty memory metric with source for shared pooled ByteBuf allocator
- [CELEBORN-1496] Differentiate map results with only different stageAttemptId
- [CELEBORN-1506][FOLLOWUP] InFlightRequestTracker should not reset totalInflightReqs for cleaning up to avoid negative totalInflightReqs for limitZeroInFlight
- [CELEBORN-1520] Minor logging fix for AppDiskUsageMetric and Fixed UTs
- [CELEBORN-1522] Fix applicationId extraction from shuffle key
- [CELEBORN-1526] Fix MR plugin can not run on Hadoop 3.1.0
- [CELEBORN-1544] ShuffleWriter needs to call close finally to avoid memory leaks
- [CELEBORN-1557] Fix totalSpace of DiskInfo for Master in HA mode
- [CELEBORN-1558] Fix the incorrect decrement of pendingWrites in handlePushMergeData
- [CELEBORN-1564] Fix actualUsableSpace of offerSlotsLoadAware condition on diskInfo
- [CELEBORN-1575] TimeSlidingHub should remove expire node when reading
- [CELEBORN-1579] Fix the memory leak of result partition
- [CELEBORN-1580] ReadBufferDispacther should notify exception to listener
- [CELEBORN-1581] Fix incorrect metrics of DeviceCelebornFreeBytes and DeviceCelebornTotalBytes
- [CELEBORN-1583] MasterClient#sendMessageInner should throw Throwable for celeborn.masterClient.maxRetries is 0
- [CELEBORN-1655] Fix read buffer dispatcher thread terminate unexpectedly
- [CELEBORN-1662] Handle PUSH_DATA_FAIL_PARTITION_NOT_FOUND in getPushDataFailCause
- [CELEBORN-1664] Fix secret fetch failures after LEADER master failover
- [CELEBORN-1665] CommitHandler should process CommitFilesResponse with COMMIT_FILE_EXCEPTION status
- [CELEBORN-1667] Fix NPE & LEAK occurring prior to worker registration
- [CELEBORN-1668] Fix NPE when handle closed file writers
- [CELEBORN-1669] Fix NullPointerException for PartitionFilesSorter#updateSortedShuffleFiles after cleaning up expired shuffle key
- [CELEBORN-1674] Fix reader thread name of MapPartitionData
- [CELEBORN-1682] Add java tools.jar into classpath for JVM quake
- [CELEBORN-1686] Avoid return the same pushTaskQueue
- [CELEBORN-1691] Fix the issue that upstream tasks don't rerun and the current task still retry when failed to decompress in flink
- [CELEBORN-1692] Set mount point in fromPbFileInfoMap
- [CELEBORN-1693] Fix storageFetcherPool concurrent problem
- [CELEBORN-1696] StorageManager#cleanFile should remove file info
- [CELEBORN-1705] Fix disk buffer size is negative issue
- [CELEBORN-1717] Fix ReusedExchangedSuit UT bug
- [CELEBORN-1718] Fix memory storage file won't hard split when memory file is full and worker has no disks
- [CELEBORN-1726] Update WorkerInfo when transition worker state
- [CELEBORN-1727] Correct the calculation of worker diskInfo actualUsableSpace
- [CELEBORN-1728] Fix NPE when failing to connect to celeborn worker

### Build

- [CELEBORN-1677] Update SCM information for SBT build configuration

### Documentation

- [CELEBORN-914][FOLLOWUP] Add emptyFilePrimaryIds and emptyFileReplicaIds of worker service log in startup document
- [CELEBORN-914][FOLLOWUP] Adding metrics for memory file storage in monitoring.md
- [CELEBORN-1058][FOLLOWUP] Update name of master service from MasterSys to Master in startup document
- [CELEBORN-1551] Fix wrong link in quota_management.md

### Dependencies

- [CELEBORN-1666] Bump scala-protoc from 1.0.6 to 1.0.7

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.5.0 version:

| Contributors |             |             |                     |                |              |
|--------------|-------------|-------------|---------------------|----------------|--------------|
| ErikFang     | Ethan Feng  | Fei Wang    | Fu Chen             | Jiashu Xiong   | Kerwin Zhang |
| Keyong Zhou  | Kun Wan     | Lianne Li   | Mridul Muralidharan | Nicholas Jiang | Sanskar Modi |
| Shaoyun Chen | Weijie Guo  | Wenliang Bo | Xianming Lei        | Xu Huang       | Yanze Jiang  |
| Yihe Li      | Yuting Wang | Zhao zhao   | Zhentao Shuai       |                |              |
