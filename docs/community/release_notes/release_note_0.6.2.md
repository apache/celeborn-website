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

# Apache Celeborn™ 0.6.2 Release Notes

## Highlight

- Introduce end to end integrity checks
- Fix uncaught exception in DataPusher
- Throw IOException when compressed data header corrupted

### Correctness

- [CELEBORN-2176] Fix uncaught exception in DataPusher
- [CELEBORN-2200] Throw IOException when compressed data header corrupted

### Improvement

- [CELEBORN-474] PushState uses JavaUtils#newConcurrentHashMap to speed up ConcurrentHashMap#computeIfAbsent
- [CELEBORN-894] End to End Integrity Checks
- [CELEBORN-1258] Introduce --show-cluster-apps-info master command to show cluster application's info
- [CELEBORN-2131] Add sorting duration logs in FileSorter
- [CELEBORN-2152] Support merge buffers on the worker side to improve memory utilization
- [CELEBORN-2154] Optimize the exception handling of DFS read to avoid tasks from getting stuck
- [CELEBORN-2155] Avoid using duplicate diskFileInfoMap functions
- [CELEBORN-2170] Refactor ByteBuffer's readToReadOnlyuffer interface
- [CELEBORN-2178] Close hadoopFs FileSystem for stopping master
- [CELEBORN-2181] Modify the shuffleAllocations order in the disk info log
- [CELEBORN-2188] Abort multipart upload for S3 and OSS in DfsTierWriter#handleException
- [CELEBORN-2189] Allow config worker pod terminationGracePeriodSeconds in chart
- [CELEBORN-2192] ReadBufferDispatcher should add timeout constraints to fast fail in case of timeout
- [CELEBORN-2195] Align log4j2.xml and metrics.properties of charts with templates
- [CELEBORN-2198] Fix NPE in tryWithTimeoutAndCallback test due to lazy deviceCheckThreadPool not initialized
- [CELEBORN-2203] Set celeborn.master.internal.endpoints in the configmap
- [CELEBORN-2208] Log the partition reader wait time if exceeds the threshold

### Stability and Bug Fix

- [CELEBORN-1983] Fix fetch fail not throw due to reach spark maxTaskFailures
- [CELEBORN-2032] Create reader should change to peer by taskAttemptId'
- [CELEBORN-2105] RpcMetricsTracker should clean up metrics for stopping Inbox
- [CELEBORN-2142] DfsTierWriter should create for unavailable disks
- [CELEBORN-2150] Fix the match condition in checkIfWorkingDirCleaned
- [CELEBORN-2153] Fix NPE problem that occurs during concurrent merge
- [CELEBORN-2159] Fix dfs storage type check in StorageManager#cleanupExpiredShuffleKey
- [CELEBORN-2163] PushDataHandler should increment WriteDataFailCount for file writer exception of MapPartition PushData
- [CELEBORN-2164] Fix incorrect filtering conditions in updateDiskInfos
- [CELEBORN-2165] Fix endless swagger openapi.json security items
- [CELEBORN-2171] Fix array index error in submitRetryPushMergedData
- [CELEBORN-2180] Fix Invalid RequestId during RegisterApplicationInfo

### Dependencies

- [CELEBORN-2167] Bump Spark from 3.5.6 to 3.5.7
- [CELEBORN-2168] Bump Flink from 1.20.2 to 1.20.3
- [CELEBORN-2173] jersey-test-framework-core dependency should exclude junit5 dependencies to execute java test cases for CI

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.6.2 version:

| Contributors  |              |             |              |                |                |
|---------------|--------------|-------------|--------------|----------------|----------------|
| Gaurav Mittal | Hai Zhou     | Jiaming Xie | Jianfu Li    | JuniverseCoder | Nicholas Jiang |
| Ping Zhang    | Shaoyun Chen | Wang Fei    | Xianming Lei | Yanze Jiang    | Zhaohui Xu     |
| Zhengqi Zhang |              |             |              |                |                |
