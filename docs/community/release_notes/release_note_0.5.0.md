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

# Apache Celeborn™ 0.5.0 Release Notes

## Highlight
- Support TLS for internal communication
- Memory usage and performance optimization
- Support Flink 1.19
- Enhanced metrics and HTTP APIs
- Support Java 21
- Support memory storage(experimental)

### Improvement
- [CELEBORN-1468] update dashboard layout for Celeborn 0.5
- [CELEBORN-1467] celeborn.worker.storage.dirs should support soft link
- [CELEBORN-1463][FOLLOWUP] Respeact client/server threads num to avoid competitiveness
- [CELEBORN-1463] Create network memory allocator with celeborn.network.memory.allocator.numArenas
- [CELEBORN-1461] Fix Celeborn ipv6 local hostname resolution
- [CELEBORN-1460][FOLLOWUP] MRAppMasterWithCeleborn support uri of absolute conf path for mapreduce.job.cache.files
- [CELEBORN-1460] MRAppMasterWithCeleborn supports setting mapreduce.celeborn.master.endpoints via environment variable CELEBORN_MASTER_ENDPOINTS
- [CELEBORN-1459] Introduce CleanTaskQueueSize and CleanExpiredShuffleKeysTime to record situation of cleaning up expired shuffle keys
- [CELEBORN-1453] Fix the thread safety bug in getMetrics
- [CELEBORN-1448] Use static regex Pattern instances in JavaUtils.timeStringAs and JavaUtils.byteStringAs
- [CELEBORN-1443] Remove ratis dependencies from common module
- [CELEBORN-1441] RocksDBLogger uses Logger#Logger(InfoLogLevel) instead of deprecated constructor of o.rocksdb.Logger
- [CELEBORN-1434] Support MRAppMasterWithCeleborn to disable job recovery and job reduce slow start by default
- [CELEBORN-1430] TransportClientFactory should check whether handler is null when creating client
- [CELEBORN-1428] WrappedRpcResponseCallback should stop timer of PrimaryPushDataTime and ReplicaPushDataTime for failure
- [CELEBORN-1427] Add Capacity metrics for Celeborn
- [CELEBORN-1425][HELM] Add helm chart unit tests to ensure manifests are rendered as expected
- [CELEBORN-1423][HELM] Refactor chart templates and update readme docs
- [CELEBORN-1422] Remove tmpRecords array when collecting written count metrics
- [CELEBORN-1421] Refine code in master to reduce unnecessary sync to get workers/lostworkers/shutdownWorkers
- [CELEBORN-1419] Avoid adding shuffle id repeatedly
- [CELEBORN-1416] Add CI for helm charts lint and test
- [CELEBORN-1415][HELM] Move all the master and worker resource manifests to dedicated directories respectively
- [CELEBORN-1414] PartitionFilesSorter resolve DiskFileInfo without sorting lock
- [CELEBORN-1406] Use Files. getLastModifiedTime to find last modified time instead of file.lastModified
- [CELEBORN-1402][FOLLOWUP] Correct document of setting spark.executor.userClassPathFirst to false
- [CELEBORN-1402] SparkShuffleManager print warning log for spark.executor.userClassPathFirst=true with ShuffleManager defined in user jar
- [CELEBORN-1401] Add SSL support for ratis communication
- [CELEBORN-1392] TransportClientFactory should regard as zero for negative celeborn.<module>.io.connectTimeout/connectionTimeout
- [CELEBORN-1390] ServletContextHandler should allow null path info to avoid redirection
- [CELEBORN-1387] Allow more retries when requesting more memory in sortbasedpusher
- [CELEBORN-1385] HttpServer support idle timeout configuration of Jetty
- [CELEBORN-1374] Refactor SortBuffer and PartitionSortedBuffer
- [CELEBORN-1372] Update ControlMessages to handle ApplicationMeta and ApplicationMetaRequest
- [CELEBORN-1371] Update ratis with internal port endpoint address as well
- [CELEBORN-1370] Exception with authentication is enabled when creating send-application-meta thread pool
- [CELEBORN-1369] Support for disable fallback to Spark's default shuffle
- [CELEBORN-1368] Log celeborn config for debugging purposes
- [CELEBORN-1365] Ensure that a client cannot update the metadata belonging to a different application
- [CELEBORN-1362] Remove unnecessary configuration celeborn.client.flink.inputGate.minMemory and celeborn.client.flink.resultPartition.minMemory
- [CELEBORN-1361] MaxInFlightPerWorker should use the value provided by PushStrategy
- [CELEBORN-1360] Ensure that a client cannot push or fetch data belonging to a different application
- [CELEBORN-1359] Support Netty Logging at the network layer
- [CELEBORN-1357] AbstractRemoteShuffleResultPartitionFactory should remove the check of shuffle compression codec
- [CELEBORN-1356] Split rpc module into rpc_app and rpc_service
- [CELEBORN-1354][FOLLOWUP] Split rpc_app into rpc_app_lifecyclemanager and rpc_app_client
- [CELEBORN-1354] auto ssl for rpc_app transport module
- [CELEBORN-1351] Introduce SSLFactory and enable TLS support
- [CELEBORN-1349] Add SSL related configs and support for ReloadingX509TrustManager
- [CELEBORN-1348] Update infrastructure for SSL communication
- [CELEBORN-1346] Add build changes and test resources for ssl support
- [CELEBORN-1341] Improve Celeborn document
- [CELEBORN-1337] Remove unused fields from HeartbeatFromApplicationResponse
- [CELEBORN-1336] Remove client partition split pool
- [CELEBORN-1328] Introduce ActiveSlotsCount metric to monitor the number of active slots
- [CELEBORN-1327] Support Spark 3.5 with JDK21
- [CELEBORN-1323] Introduce ShutdownWorkerCount metric to record the count of workers in shutdown list
- [CELEBORN-1322] Rename LostWorkers metric to LostWorkerCount to align the naming style
- [CELEBORN-1321] Change noisy expire shuffle log to debug level and aggregate log
- [CELEBORN-1320] Use ReviveManager for soft splits
- [CELEBORN-1317][FOLLOWUP] Update default value of celeborn.master.http.maxWorkerThreads and celeborn.worker.http.maxWorkerThreads via QueuedThreadPool
- [CELEBORN-1317][FOLLOWUP] ServerConnector supports celeborn.master.http.stopTimeout and celeborn.worker.http.stopTimeout
- [CELEBORN-1317][FOLLOWUP] HttpServer avoid Jetty's acceptor thread shrink for stopping
- [CELEBORN-1317] Refine celeborn http server and support swagger ui
- [CELEBORN-1314] add capacity-bounded inbox for rpc endpoint
- [CELEBORN-1313] Custom Network Location Aware Replication
- [CELEBORN-1309][FOLLOWUP] Cap the max memory can be used for sort buffer
- [CELEBORN-1309] Support adaptive management of memory threshold for SortBasedWriter
- [CELEBORN-1307][FOLLOWUP] Introduce worker detail module for dashboard frontend
- [CELEBORN-1307] Introduce worker module for dashboard frontend
- [CELEBORN-1306] Introduce master module for dashboard frontend
- [CELEBORN-1305][FOLLOWUP] Unify application module naming
- [CELEBORN-1305][FOLLOWUP] Unify application module naming
- [CELEBORN-1305] Introduce application module for dashboard frontend
- [CELEBORN-1304] Introduce tenant module for dashboard frontend
- [CELEBORN-1303] Introduce API request module for dashboard frontend
- [CELEBORN-1302] Introduce overview module for dashboard frontend
- [CELEBORN-1299] Introduce JVM profiling in Celeborn Worker using async-profiler
- [CELEBORN-1297] Change DB script column from user to name
- [CELEBORN-1296] Introduce celeborn.dynamicConfig.store.fs.path config to configure the path of dynamic config file for fs store backend
- [CELEBORN-1293] Output received signals at master and worker
- [CELEBORN-1287] Improve both combine and sort operation of shuffle read for CelebornShuffleReader
- [CELEBRON-1285] Add check tenantConfig.getConfigs().isEmpty() in getTenantUserConfigFromCache
- [CELEBORN-1277] Add celeborn.quota.enabled at Master and Client side to enable checking quota
- [CELEBORN-1276] Move checkQuotaSpaceAvailable from Quota to QuotaManager
- [CELEBORN-1273] Move java classes under scala src to java
- [CELEBORN-1267] Add config to control worker check in CelebornShuffleFallbackPolicyRunner
- [CELEBORN-1265][FOLLOWUP] Remove unnecessary GlutenShuffleDependencyHelper
- [CELEBORN-1265] Fix batches read metric for gluten columnar shuffle
- [CELEBORN-1264] ConfigService supports TENANT_USER config level
- [CELEBORN-1261] Add auth support to client
- [CELEBORN-1257][FOLLOWUP] Removed the additional secured port from Celeborn Master
- [CELEBORN-1257] Adds a secured port in Celeborn Master for secure communication with LifecycleManager
- [CELEBORN-1256] Added internal port and auth support to Celeborn worker
- [CELEBORN-1254][FOLLOWUP] Rename celeborn.worker.sortPartition.reservedMemory.enabled to celeborn.worker.sortPartition.prefetch.enabled
- [CELEBORN-1254] PartitionFilesSorter seeks to position of each block and does not warm up for non-hdfs files
- [CELEBORN-1251] Connect the server and client bootstraps to RpcEnv
- [CELEBORN-1249] Add LICENSE of Celeborn Web
- [CELEBORN-1245][FOLLOWUP] Fix SendWorkerEvent in HA mode
- [CELEBORN-1245] Support Celeborn Master(Leader) to manage workers
- [CELEBORN-1242] Unify celeborn thread name format
- [CELEBORN-1241][FOLLOWUP] Fix duplicate CelebornRackResolver issue for SingleMasterMetaManager
- [CELEBORN-1241] Introduce hot load for CelebornRackResolver
- [CELEBORN-1240][FOLLOWUP] Web lint check uses different groups
- [CELEBORN-1240] Introduce Husky Configuration to Celeborn Web
- [CELEBORN-1239][FOLLOWUP] Deprecate celeborn.quota.configuration.path config
- [CELEBORN-1239] Celeborn QuotaManager support use ConfigService and support default quota setting
- [CELEBORN-1237] Refactor metrics name
- [CELEBORN-1236][FOLLOWUP] Gauge is_terminating, is_terminated and is_shutdown should represent a single numerical value
- [CELEBORN-1235] Start test nodes in random ports to allow multiple builds run in the same ci server
- [CELEBORN-1234] Master should persist the application meta in Ratis and push it to the Workers
- [CELEBORN-1232] Add Menu to Celeborn Web
- [CELEBORN-1231] Support baseline implementation of Celeborn Web
- [CELEBORN-1230] Check working directory read and write error without init delay
- [CELEBORN-1229] Support for application registration with Celeborn Master
- [CELEBORN-1213] Add pronunciation of Celeborn in README.md
- [CELEBORN-1212] Support for Anonymous SASL Mechanism
- [CELEBORN-1209] Print Warning Log if User use Celeborn with enabled Spark ShuffleTracking
- [CELEBORN-1208] Unify parse uniqueId to WorkerInfo
- [CELEBORN-1195] Use batch rack resolve when restore meta from file
- [CELEBORN-1179] Add support in Celeborn Workers to fetch application meta from the Master
- [CELEBORN-1172] Support dynamic switch shuffle push write mode based on partition number
- [CELEBORN-1144] Batch OpenStream RPCs
- [CELEBORN-1133] Refactor fileinfo
- [CELEBORN-1078] Log info to indicate columnar shuffle writer take effect
- [CELEBORN-1054] Support db based dynamic config service
- [CELEBORN-1051] Add isDynamic property for CelebornConf
- [CELEBORN-1012] Add a dedicated internal port in Master to talk to Workers and other Masters
- [CELEBORN-914] Support memory file storage
- [MINOR] Update log level of CommitFiles success for CommitHandler from error to info
- [MINOR] Unifiy license format of pom.xml
- [MINOR] Improve SuiteJ of client-flink module
- [MINOR] Fix typos
- [MINOR] Fix typo in TransportClient
- [INFRA][FOLLOWUP] Fix copyright of mkdocs.yml for graduation

### Stability and Bug Fix
- [CELEBORN-1462] Fix layout of DeviceCelebornTotalBytes, DeviceCelebornFreeBytes, RunningApplicationCount and DecommissionWorkerCount in celeborn-dashboard.json
- [CELEBORN-1456] Fix LICENSE dependencies in LICENSE-binary
- [CELEBORN-1456][FOLLOWUP] Fix license issue
- [CELEBORN-1450] MRAppMasterWithCeleborn should get FileSystem via mapreduce.job.dir for HDFS federation
- [CELEBORN-1449] Fix JavaUtils#deleteRecursivelyUsingJavaIO to skip non-existing file input
- [CELEBORN-1424] Fix getChunk NPE when enable local read
- [CELEBORN-1393][HELM] Resource labels and selector labels are duplicated
- [CELEBORN-1317][FOLLOWUP] Fix threadDump UT stuck issue
- [CELEBORN-1317][FOLLOWUP] Improve parameters, description and document of REST API
- [CELEBORN-1317][FOLLOWUP] Retry to setup mini cluster if the cause isBindException
- [CELEBORN-1310][FLINK] Support Flink 1.19
- [CELEBORN-1280] Change default value of celeborn.worker.graceful.shutdown.recoverDbBackend to ROCKSDB
- [CELEBORN-1270] Introduce PbPackedPartitionLocations to (de-)serialize PartitionLocations more efficiently
- [CELEBORN-1222] Fix Celeborn worker won't record HDFS writer
- [CELEBORN-1016] Fix IPv6 host address resolve issue
- [MINOR] Fix typos and wrong package name
- [MINOR] Fix typos in profile name when checking dependencies

### Build
- [CELEBORN-1438] Exclude celeborn-service_xx-test jar
- [CELEBORN-1331] Remove third-party dependencies in shaded clients' pom
- [CELEBORN-1263] Fix Master HA mode without internal port error

### Documentation
- [CELEBORN-1369][FOLLOWUP] Improve docs for shuffle fallback policy
- [CELEBORN-1353] Document Celeborn security - authentication and SSL support
- [CELEBORN-1341][FOLLOWUP] Improve Celeborn document
- [CELEBORN-1317][FOLLOWUP] Remove Incubating from REST API Documentation
- [CELEBORN-1311] Developers Doc introduce Slots allocation
- [CELEBORN-1286] Introduce configuration.md to document dynamic config and config service
- [CELEBORN-1284][FOLLOWUP] Fix license style of quota_management.md
- [CELEBORN-1284][DOC] Add document about QuotaManager based on ConfigService
- [CELEBORN-1134][FOLLOWUP] Add execution.batch-shuffle-mode: ALL_EXCHANGES_BLOCKING to Flink Configuration of Deploy Flink client
- [MINOR] Fix typo in developer docs - overview
- [MINOR] Fix style and Gluten link in Developers Doc

### Dependencies
- [CELEBORN-1400] Bump Ratis version from 2.5.1 to 3.0.1
- [CELEBORN-1396] Bump Netty from 4.1.107.Final to 4.1.109.Final
- [CELEBORN-1395] Bump RoaringBitmap version from 1.0.5 to 1.0.6
- [CELEBORN-1394] Bump Spark from 3.4.2 to 3.4.3
- [CELEBORN-1389] Bump Dropwizard version from 3.2.6 to 4.2.25
- [CELEBORN-1382] Bump RoaringBitmap version from 0.9.32 to 1.0.5
- [CELEBORN-1281] Bump Spark from 3.5.0 to 3.5.1
- [CELEBORN-1262] Bump Spark from 3.3.3 to 3.3.4
- [CELEBORN-1243] Bump Flink from 1.18.0 to 1.18.1
- [CELEBORN-1221] Bump Flink from 1.17.0 to 1.17.2
- [BUILD] Bump netty version to latest 4.1.107.Final

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.5.0 version:

| Contributors         |               |                |             |              |             |
|----------------------|---------------|----------------|-------------|--------------|-------------|
| albin3               | AngersZhuuuu  | Aravind Patnam | binjie yang | cfmcgrady    | ChenYi015   |
| CodingCat            | Curtis Howard | cxzl25         | ErikFang    | FMX          | ForVic      |
| huangxiaopingRD      | jiaoqingbo    | kerwin-zk      | labbomb     | leixm        | miyuesc     |
| Mridul Muralidharan  | mridulm       | onebox-li      | otterc      | pan3793      | radeity     |
| RexXiong             | SteNicholas   | tiny-dust      | turboFei    | waitinfuture | xianminglei |
| xinyuwang1           | zwangsheng    |                |             |              |             |

