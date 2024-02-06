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

# Apache Celeborn(Incubating) 0.4.0 Release Notes

## Highlight

- Rerun Spark Stage for Celeborn Shuffle Fetch Failure
- Added support for Apache Hadoop MapReduce
- Added support for Apache Flink 1.18
- Implemented JVM monitoring in Celeborn Worker using JVMQuake
- Added support for SBT build system

### IMPOROVEMENT

- [CELEBORN-299] Deprecate `celeborn.worker.storage.baseDir.prefix` and `celeborn.worker.storage.baseDir.number`
- [CELEBORN-313] Add rest endpoint to show master group info
- [CELEBORN-448] Support exclude worker manually
- [CELEBORN-655][SPARK] Rename newAppId to appUniqueId
- [CELEBORN-733] Clean unused GetBlacklist & GetBlacklistResponse
- [CELEBORN-772] Convert StreamChunkSlice, ChunkFetchRequest, TransportableError to PB
- [CELEBORN-780] Change SPARK_SHUFFLE_FORCE_FALLBACK_PARTITION_THRESHOLD default to Int.MaxValue since slot's is not a bottleneck
- [CELEBORN-781] Refactor RPC message type name
- [CELEBORN-794] Fix link of CONFIGURATIONS in README
- [CELEBORN-808] Remove unnecessary RssShuffleManager in 0.4.0
- [CELEBORN-815] Remove unused ShuffleClient.readPartition
- [CELEBORN-829] Improve response message of invalid HTTP request
- [CELEBORN-833] Remove unused code
- [CELEBORN-835] Format specifiers should be used instead of string concatenation
- [CELEBORN-851] Mention Celeborn 0.4 server requires 0.3 or above clients
- [CELEBORN-856] Add mapreduce integration test
- [CELEBORN-868][MASTER] Adjust logic of SlotsAllocator#offerSlotsLoadAware fallback to roundrobin
- [CELEBORN-879] Add `dev/dependencies.sh` for audit dependencies
- [CELEBORN-885][SPARK] Shade RoaringBitmap to avoid dependency conflicts
- [CELEBORN-887] Option --config should take effect in celeborn-daemon.sh script
- [CELEBORN-891] Remove pipeline feature for sort based writer
- [CELEBORN-903][INFRA] Fix list index out of range for JIRA resolution in merge_pr
- [CELEBORN-907][INFRA] The Jira Python misses our assignee when it searches users again
- [CELEBORN-910][INFRA] Support JIRA_ACCESS_TOKEN for merging script
- [CELEBORN-912] Support build with Spark 3.5
- [CELEBORN-913] Implement method ShuffleDriverComponents#supportsReliableStorage
- [CELEBORN-918][INFRA] Auto Assign First-time contributor with Contributors role
- [CELEBORN-919] Move Columnar Shuffle code into an individual module
- [CELEBORN-929][INFRA] Add dependencies check CI
- [CELEBORN-930][INFRA] Eagerly check if the token is valid to align with the behavior of username/password auth
- [CELEBORN-931][INFRA] Fix merged pull requests resolution
- [CELEBORN-936] Shuffle master urls to avoid always connect first mast…
- [CELEBORN-937][INFRA] Improve branch suggestion for backporting
- [CELEBORN-939] Change register to unregister in Log
- [CELEBORN-940] Make the number of arguments and placeholders consistent
- [CELEBORN-951] Add IssueNavigationLink and icon for IDEA
- [CELEBORN-953] Remove unused-imports in Utils.scala
- [CELEBORN-973] Improve HttpRequestHandler handle HTTP request with base, master and worker
- [CELEBORN-977] Support RocksDB as recover DB backend
- [CELEBORN-978] Improve dependency.sh replacement mode
- [CELEBORN-980] Asynchronously delete original files to fix `ReusedExchange` bug
- [CELEBORN-983] Rename PrometheusMetric configuration
- [CELEBORN-985] Change default value of numConnectionsPerPeer to 1
- [CELEBORN-999] MR deps check
- [CELEBORN-1000] MR module style check
- [CELEBORN-1006] Add support for Apache Hadoop 2.x in Celeborn build
- [CELEBORN-1018] Fix throw exception when exec create-package.sh script
- [CELEBORN-1028] Make prometheus path configurable
- [CELEBORN-1038] Clean up deprecated api
- [CELEBORN-1044] Enhance the check of parameter array length
- [CELEBORN-1047] Remove conf `celeborn.worker.sortPartition.eagerlyRemoveOriginalFiles.enabled`
- [CELEBORN-1052] Introduce dynamic ConfigService at SystemLevel and TenantLevel
- [CELEBORN-1060] Fix the master's http port conflicts with rpc port in celeborn default template file
- [CELEBORN-1065] Prevent the local variable 'time' declared in one 'switch' branch and used in another
- [CELEBORN-1066] Skip looping streamimg sets in numShuffleSteams of ChunkStreamManager
- [CELEBORN-1068] Fix hashCode computation
- [CELEBORN-1069] Avoid double brace initialization
- [CELEBORN-1070] Add error-prone to pom.xml
- [CELEBORN-1071] Ensure guardedBy is satisfied, fix DCL bugs as well
- [CELEBORN-1072] Fix misc error prone reports found
- [CELEBORN-1079] Fix use of GuardedBy in client-flink/common
- [CELEBORN-1082] Fixing partition sorter task failures due to duplicate sorting
- [CELEBORN-1087] Remove SimpleStateMachineStorageUtil in master module
- [CELEBORN-1095] Support configuration of fastest available XXHashFactory instance for checksum of Lz4Decompressor
- [CELEBORN-1100] Introduce ChunkStreamCount, OpenStreamFailCount metrics about opening stream of FetchHandler
- [CELEBORN-1106] Ensure data is written into flush buffer before sending message to client
- [CELEBORN-1108] Rat plugin check for more modules
- [CELEBORN-1122] Metrics supports json format
- [CELEBORN-1123] Support fallback to non-columnar shuffle for schema that cannot be obtained from shuffle dependency
- [CELEBORN-1127] Add JVM classloader metrics
- [CELEBORN-1131] Add Client/Server bootstrap framework to transport layer
- [CELEBORN-1134] Celeborn Flink client should validate whether execution.batch-shuffle-mode is ALL_EXCHANGES_BLOCKING
- [CELEBORN-1135] Added tests for the RpcEnv and related classes
- [CELEBORN-1140] Use try-with-resources to avoid FSDataInputStream not being closed
- [CELEBORN-1142] clear shuffleIdCache in shutdown method of ShuffleClientImpl
- [CELEBORN-1145] Separate clientPushBufferMaxSize from CelebornInputStreamImpl
- [CELEBORN-1147] Added a dedicated API for RPC messages which also accepts an RpcResponseCallback instance
- [CELEBORN-1149] Improve replica selection when rack aware
- [CELEBORN-1150] support io encryption for spark
- [CELEBORN-1151] Request slots when register shuffle should filter the workers excluded by application
- [CELEBORN-1152] fix GetShuffleId RPC NPE for empty shuffle
- [CELEBORN-1157] Add client-side support for Sasl Authentication in the transport layer
- [CELEBORN-1162][BUG] Fix refCnt 0 Exception in FetchHandler#handleChunkFetchRequest
- [CELEBORN-1164] Introduce FetchChunkFailCount metric to expose the count of fetching chunk failed in current worker
- [CELEBORN-1176] Server side support for Sasl Auth
- [CELEBORN-1177] OpenStream should register stream via ChunkStreamManager to close stream for ReusedExchange
- [CELEBORN-1180] Changed the version of Sasl Auth related config to 0.5
- [CELEBORN-1187] Unify the size and file count of active shuffle metrics for master and worker
- [CELEBORN-1188][TEST] Using JUnit function instead of java assert
- [CELEBORN-1189] Introduce RunningApplicationCount metric and /applications API to record running applications of worker
- [CELEBORN-1190] Apply error prone patch and suppress some problems
- [CELEBORN-1193] ResettableSlidingWindowReservoir should reset `full` to false
- [CELEBORN-1196] Slots allocator will increment disk index repeatedly
- [CELEBORN-1201] Optimize memory usage of cache in partition sorter
- [CELEBORN-1210] Fix potential memory leak in PartitionFilesCleaner
- [CELEBORN-1211] Add extension for celeborn shuffle handler
- [CELEBORN-1214] Introduce WriteDataHardSplitCount metric to record HARD_SPLIT partitions of PushData and PushMergedData
- [CELEBORN-1215] Introduce PausePushDataAndReplicateTime metric to record time for a worker to stop receiving pushData from clients and other workers
- [CELEBORN-1216] Resolve error occurring during distribution creation with profile -Pspark-2.4
- [CELEBORN-1217] Improve exception message of loadFileGroup for ShuffleClientImpl
- [CELEBORN-1218] Optimize dataPusher to get partitionLocationMap only once
- [CELEBORN-1219] takeBuffer() avoid checking source.metricsCollectCriticalEnabled twice
- [CELEBORN-1220][IMPROVEMENT] Make trim logic more robust
- [CELEBORN-1224] Make TransportMessage#type transient for backward compatibility
- [CELEBORN-1225] Worker should build replicate factory to get client for sending replicate data
- [CELEBORN-1226][BRANCH-0.4.0] Unify creation of thread using ThreadUtils (#2245)
- [CELEBORN-1228] Format the timestamp when recording worker failure
- [CELEBORN-1233] Treat unfound PartitionLocation as failed in Controller#commitFiles
- [CELEBORN-1236][METRICS] Celeborn add metrics about thread pool
- [CELEBORN-1238] deviceCheckThreadPool is only initialized when diskCheck is enabled
- [CELEBORN-1242] Unify celeborn thread name format
- [CELEBORN-1252] Fix resource consumption of worker does not update when update interval is greater than heartbeat interval
- [CELEBORN-1253] Improve exception message of fetching chunk failure for WorkerPartitionReader

### BUILD

- [CELEBORN-836][BUILD] Initial support sbt
- [CELEBORN-843][BUILD] `sbt` support flink-related module build/test
- [CELEBORN-850][INFRA] Add SBT CI
- [CELEBORN-867][BUILD] Add maven local repository to sbt respositories
- [CELEBORN-880] Remove sbt compiler plugin `genjavadoc-plugin`
- [CELEBORN-884][BUILD] Consolidate all dependencies into a global object `Dependencies`
- [CELEBORN-898][INFRA] Fix java.lang.NoClassDefFoundError: org/hamcrest/SelfDescribing for SBT CI
- [CELEBORN-906][BUILD] Aligning dependencies between SBT and Maven
- [CELEBORN-921] Upgrade sbt to 1.9.4
- [CELEBORN-989] Add support for making distribution package via SBT
- [CELEBORN-1002] Add SBT MRClientProject
- [CELEBORN-1031] SBT correct the LICENSE and NOTICE for shaded client jars
- [CELEBORN-1156][BUILD] SBT publish support
- [CELEBORN-1159][BUILD] Update the scope of the `protobuf-java` dependency from `protobuf` to `runtime`
- [CELEBORN-1191] Migrate the release script from Maven to SBT
- [CELEBORN-1194] Add sbt-pgp plugin for publishing signed artifacts
- [CELEBORN-1198] Keep debug info when use SBT build
- [CELEBORN-1199] Add LICENSE and NOTICE files for service related sub-projects
- [CELEBORN-1203] Add `LicenseAndNoticeMergeStrategy` to resolve inner project LICENSE/NOTICE conflict for shaded client packaging
- [CELEBORN-1205] Disable Maven local caches to improve SBT building stability

### Documentation

- [CELEBORN-822] Add quick start guide
- [CELEBORN-822] Introduce a quick start guide for running Apache Flink with Apache Celeborn
- [CELEBORN-823] Add Celeborn architecture document
- [CELEBORN-824] Add PushData document
- [CELEBORN-826] Add storage document
- [CELEBORN-828] Merge Monitoring to Development doc
- [CELEBORN-831] Add traffic control document
- [CELEBORN-834] Add fault tolerant document
- [CELEBORN-849] Document on Master
- [CELEBORN-853] Document on LifecycleManager
- [CELEBORN-860] Document on ShuffleClient
- [CELEBORN-864] Document on blacklist
- [CELEBORN-869] Document on Integrating Celeborn
- [CELEBORN-870] Document on usage together with Gluten
- [CELEBORN-877] Document on SBT
- [CELEBORN-893] Fix Spark patch list text in Readme
- [CELEBORN-909] Mention `celeborn.worker.directMemoryRatioToResume` default value changed in main/0.4
- [CELEBORN-927] Correct celeborn.metrics.conf.*.sink.csv.class configuration example for a CSV sink
- [CELEBORN-927] Guideline to add new RPC messages
- [CELEBORN-927] Run dev/reformat before you create a new pull request for code style
- [CELEBORN-948] fix quick start doc about failed to submit flink wordcount
- [CELEBORN-954] Add documentation about reliable shuffle data storage
- [CELEBORN-974] Add quick start guide about using MapReduce with Celeborn
- [CELEBORN-987] README#Build should extend to Java8/11/17
- [CELEBORN-991] Remove incorrect `spark.metrics.conf`
- [CELEBORN-997] Fix Rolling upgrade broken link
- [CELEBORN-1010] Update docs about `spark.shuffle.service.enabled`
- [CELEBORN-1085] Update metrics doc
- [CELEBORN-1091] Update docs about how to use error-prone plugin in IDEA
- [CELEBORN-1101] Update metrics doc to add necessary configuration steps
- [CELEBORN-1104] Fix SBT documentation incorrect command
- [CELEBORN-1207] SBT http repository documentation
- [CELEBORN-1223] Align master and worker metrics of document with MasterSource and WorkerSource
- [CELEBORN-1247] Output config's alternatives to doc
- [MINOR] Add the Celeborn Helm Chart doc
- [MINOR] Updated sbt.md documentation to be consistent with description

### Dependencies

- [CELEBORN-742][BUILD] Bump Hadoop 3.2.4
- [CELEBORN-821][BUILD] Bump junit from 4.12 to 4.13.2
- [CELEBORN-1113] Bump Hadoop client version from 3.2.4 to 3.3.6
- [CELEBORN-1125] Bump guava from 14.0.1 to 32.1.3-jre
- [CELEBORN-1163] Upgrade protobuf from 3.19.2 to 3.21.7
- [CELEBORN-1169] Bump Spark from 3.4.1 to 3.4.2
- [CELEBORN-1170] Upgrade snappy-java from 1.1.8.2 to 1.1.10.5
- [CELEBORN-1173] Upgrade netty version from 4.1.93.Final to 4.1.101.Final
- [CELEBORN-1184] Update the snakeyaml version from 1.33 to 2.2
- [MINOR] upgrade snappy-java from 1.1.8.2 to 1.1.10.5

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn(Incubating) 0.4.0-incubating version:

| Contributors |                |               |             |                     |              |
|--------------|----------------|---------------|-------------|---------------------|--------------|
| Angerszhuuuu | Aravind Patnam | Chandni Singh | Cheng Pan   | Erik.fang           | Fei Wang     |
| Fu Chen      | Kent Yao       | Kerwin Zhang  | Keyong Zhou | Mridul Muralidharan | Shuang       |
| SteNicholas  | Xiduo You      | exmy          | gaochao0509 | hongzhaoyang        | jiaoqingbo   |
| liangbowen   | liangyongyuan  | mingji        | onebox-li   | pengqli             | qinrui       |
| sychen       | wangshengjie   | xianminglei   | xiyu.zk     | xleoken             | zhouyifan279 |
| zml1206      | zwangsheng     |               |             |                     |              |
