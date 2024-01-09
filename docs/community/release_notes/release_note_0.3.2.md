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
      http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
---

# Apache Celeborn(Incubating) 0.3.2 Release Notes

## Highlight

- Cleanup parmap to avoid creating too many threads
- Support arbitary Ratis configs and client rpc timeout
- Support policy for master to assign slots fallback to roundrobin with no available slots
- Use scheduleWithFixedDelay instead of scheduleAtFixedRate in threads pool
- Optimize mechanism of ChunkManager expired shuffle key cleanup
- Thread factory uncaught exception and MasterNotLeaderException improvements
- Metrics and Grafana Dashboard improvements
- Fix incorrect output for metrics of Prometheus
- Fix map task hangs at limitZeroInFlight due to duplicate onFailure called

### Improvement

- [CELEBORN-665] Skip empty app snapshot logs
- [CELEBORN-678] MapperAttempts for a shuffle should reply MAP_ENDED when mapper has already been ended from speculative task
- [CELEBORN-688] Add JVM metrics grafana template
- [CELEBORN-872] Extract the same allocation logic for both loadaware and roundrobin
- [CELEBORN-916] Add new metric about active shuffle file count in worker
- [CELEBORN-1017] Make checkPushTimeout and checkFetchTimeout conditions independent
- [CELEBORN-1019] Avoid magic strings copy from Spark SQLConf
- [CELEBORN-1021] Celeborn support arbitary Ratis configs and client rpc timeout
- [CELEBORN-1024] Thread factory should set UncaughtExceptionHandler to handle uncaught exception
- [CELEBORN-1026] Optimize registerShuffle fallback log
- [CELEBORN-1030] Improve the logic of delete md5 files when initializing SimpleStateMachineStorage
- [CELEBORN-1032] Use scheduleWithFixedDelay instead of scheduleAtFixedRate in threads pool of master and worker
- [CELEBORN-1033] MasterNotLeaderException should provide the cause of exception
- [CELEBORN-1034] Offer slots uses random range of available workers instead of shuffling
- [CELEBORN-1035] Expose RunningApplicationCount, PartitionWritten and PartitionFileCount metric by Celeborn master
- [CELEBORN-1040] Adjust local read logs and refine createReader
- [CELEBORN-1041] Improve the implementation for get the PartitionIdPassthrough class
- [CELEBORN-1042] Calculate duration using nanotime in CelebornInputStream
- [CELEBORN-1043] Convert variable ‘metric’ from String to StringBuilder in toMetric method
- [CELEBORN-1048] Align fetchWaitTime metrics to spark implementation
- [CELEBORN-1058] Support specifying the number of dispatcher threads for each role
- [CELEBORN-1076] Using text/plain content type for prometheus metrics
- [CELEBORN-1077] Support to apply base legend format for all grafana metrics
- [CELEBORN-1081] Client support `celeborn.storage.activeTypes` config
- [CELEBORN-1083] Refine TimeSlidingHub code
- [CELEBORN-1084] Initialize `workerSource` member to prevent `NullPointException`
- [CELEBORN-1088] Define `baseLegend` variable for JVM Metrics dashboard
- [CELEBORN-1089] Seperate overHighWatermark check to a dedicated thread
- [CELEBORN-1093] Improve setup endpoint
- [CELEBORN-1094] Optimize mechanism of ChunkManager expired shuffle key cleanup to avoid memory leak
- [CELEBORN-1096] Avoid initializing SortShuffleManager when stop
- [CELEBORN-1097] Optimize the retrieval of configuration in the internalCreateClient
- [CELEBORN-1098] Logging worker address with worker failure log
- [CELEBORN-1099] Check register when handleGetReducerFileGroup
- [CELEBORN-1102] Optimize the performance of getAllPrimaryLocationsWithMinEpoch
- [CELEBORN-1107] Make the max default number of netty threads configurable
- [CELEBORN-1109] Cache RegisterShuffleResponse to improve the processing speed of LifecycleManager
- [CELEBORN-1110] Support celeborn.worker.storage.disk.reserve.ratio to configure worker reserved ratio for each disk
- [CELEBORN-1111] Supporting connection to HDFS with Kerberos authentication enabled
- [CELEBORN-1112] Inform celeborn application is shutdown, then celeborn cluster can release resource immediately
- [CELEBORN-1114] Remove allocationBuckets from WorkerInfo and refactor SLOTS_ALLOCATED metrics
- [CELEBORN-1116] Read authentication configs from `HADOOP_CONF_DIR`
- [CELEBORN-1121] Improve WorkerInfo#hashCode method
- [CELEBORN-1123] Support fallback to non-columnar shuffle for schema that cannot be obtained from shuffle dependency
- [CELEBORN-1126] Set kubernetes resources field for master and worker init container for helm chart
- [CELEBORN-1129] More easy to dedicate createReaderWithRetry error
- [CELEBORN-1130] LifecycleManager#requestWorkerReserveSlots should check null for endpoint
- [CELEBORN-1136] Support policy for master to assign slots fallback to roundrobin with no available slots
- [CELEBORN-1137] Correct suggested leader of exception message for MasterNotLeaderException
- [CELEBORN-1138] Fix log error in createReaderWithRetry method
- [CELEBORN-1160] Avoid calling parmap when commit files
- [CELEBORN-1165] Avoid calling parmap when reserve slots
- [CELEBORN-1166] Avoid calling parmap when setup endpoint
- [CELEBORN-1167] Avoid calling parmap when destroy slots
- [CELEBORN-1178] Destroy fail reserved slots in LifecycleManager#reserveSlotsWithRetry
- [CELEBORN-1181] Filter out null endpoint workers in destroySlotsWithRetry
- [CELEBORN-1197] Avoid using the sleep command with the s suffix in bash scripts

### Stability and Bug Fix

- [CELEBORN-1036] Map task hangs at limitZeroInFlight due to duplicate onFailure called
- [CELEBORN-1037] Incorrect output for metrics of Prometheus
- [CELEBORN-1046] Add an expiration time configuration for app directory to clean up
- [CELEBORN-1059] Fix callback not update if push worker excluded during retry
- [CELEBORN-1075] Refactor `MetricsSystem` and `AbstractSource` to use synchronized blocks
- [CELEBORN-1086] Fix JVM metrics grafana expression issue
- [CELEBORN-1103] only clean up expire data for good disks
- [CELEBORN-1120] ShuffleClientImpl should close batchReviveRequestScheduler of ReviveManager
- [CELEBORN-1124] Exclude workers of shuffle manager remove worker of connect exception primary or replica
- [CELEBORN-1128] Fix incorrect method reference in ConcurrentHashMap.contains
- [CELEBORN-1139] Master's follower clean state before install snapshot
- [CELEBORN-1143] SortBasedPusher pushData should inc memory spill metrics
- [CELEBORN-1154] Fix NPE in DeviceMonitor#readWriteError
- [CELEBORN-1192] Celeborn wait task timeout error message should show correct corresponding batch and target host and port

### Documentation

- [CELEBORN-858] Generate patch to each Spark 3.x minor version
- [CELEBORN-1003] Add missing protobuf license file into spark shaded client jars
- [CELEBORN-1020] Remove outdated info in README.md file
- [CELEBORN-1202] LICENSE mentions third-party components under other open source licenses

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn(Incubating) 0.3.2-incubating version:

| Contributors |             |             |               |                |             |
|--------------|-------------|-------------|---------------|----------------|-------------|
| Angerszhuuuu | Binjie Yang | Bowen Song  | Chandni Singh | Cheng Pan      | Ethan Feng  |
| Fei Wang     | Fu Chen     | Jiaan Geng  | Jiashu Xiong  | Jiayi Liu      | Junjie Sun  |
| Kerwin Zhang | Keyong Zhou | Luke Yan    | Marwan Salem  | Nicholas Jiang | Qingbo Jiao |
| Shaoyun Chen | Wei Tong    | Yanze Jiang | Yihe Li       | Yongyuan Liang | qinrui      |
| wusphinx     | xleoken     | 吴祥平         | 宪英            |
