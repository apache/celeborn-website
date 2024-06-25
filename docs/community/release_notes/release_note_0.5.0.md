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
- Enhanced metrics and HTTP APIs
- Support Spark3.5 with JDK21
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
- [CELEBORN-1412] celeborn.client.rpc.*.askTimeout should fallback to celeborn.rpc.askTimeout
- [CELEBORN-1411] Change default log level to INFO when there is no log4j2 config file
- [CELEBORN-1410] Combine multiple ShuffleBlockInfo into a single ShuffleBlockInfo
- [CELEBORN-1409] CommitHandler commitFiles RPC supports separate timeout configuration
- [CELEBORN-1408] workerShuffleCommitTimeout should use millisecond units
- [CELEBORN-1407] Change log4j2 template appender to file
- [CELEBORN-1406] Use Files. getLastModifiedTime to find last modified time instead of file.lastModified
- [CELEBORN-1402][FOLLOWUP] Correct document of setting spark.executor.userClassPathFirst to false
- [CELEBORN-1402] SparkShuffleManager print warning log for spark.executor.userClassPathFirst=true with ShuffleManager defined in user jar
- [CELEBORN-1401] Add SSL support for ratis communication
- [CELEBORN-1398] Support return leader ip to client
- [CELEBORN-1392] TransportClientFactory should regard as zero for negative celeborn.<module>.io.connectTimeout/connectionTimeout
- [CELEBORN-1390] ServletContextHandler should allow null path info to avoid redirection
- [CELEBORN-1387] Allow more retries when requesting more memory in sortbasedpusher
- [CELEBORN-1386] LevelDBProvider/RocksDBProvider should create non-existent multi-level directory for LevelDB/RocksDB initialization
- [CELEBORN-1385] HttpServer support idle timeout configuration of Jetty
- [CELEBORN-1384] Manually excluding workers should not depend on whether the workers are alive
- [CELEBORN-1381] Avoid construct TransportConf when creating CelebornInputStream
- [CELEBORN-1380][FOLLOWUP] leveldbjni uses org.openlabtesting.leveldbjni to support linux aarch64 platform for leveldb via aarch64 profile
- [CELEBORN-1380][FOLLOWUP] Add org.openlabtesting.leveldbjni in BSD 3-clause of LICENSE-binary
- [CELEBORN-1379] Catch Throwable for ReadBufferDispatcher thread
- [CELEBORN-1376] Push data failed should always release request body
- [CELEBORN-1374] Refactor SortBuffer and PartitionSortedBuffer
- [CELEBORN-1372] Update ControlMessages to handle ApplicationMeta and ApplicationMetaRequest
- [CELEBORN-1371] Update ratis with internal port endpoint address as well
- [CELEBORN-1370] Exception with authentication is enabled when creating send-application-meta thread pool
- [CELEBORN-1369] Support for disable fallback to Spark's default shuffle
- [CELEBORN-1368] Log celeborn config for debugging purposes
- [CELEBORN-1365] Ensure that a client cannot update the metadata belonging to a different application
- [CELEBORN-1363] AbstractRemoteShuffleInputGateFactory supports celeborn.client.shuffle.compression.codec to configure compression codec
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
- [CELEBORN-1345] Add a limit to the master's estimated partition size
- [CELEBORN-1341] Improve Celeborn document
- [CELEBORN-1339] Mark connection as timedOut in TransportClient.close
- [CELEBORN-1337] Remove unused fields from HeartbeatFromApplicationResponse
- [CELEBORN-1336] Remove client partition split pool
- [CELEBORN-1328] Introduce ActiveSlotsCount metric to monitor the number of active slots
- [CELEBORN-1327] Support Spark 3.5 with JDK21
- [CELEBORN-1326] FakedRemoteInputChannel use task name of RemoteShuffleInputGateDelegation as owningTaskName
- [CELEBORN-1324] Remove unused PrometheusSink class
- [CELEBORN-1323] Introduce ShutdownWorkerCount metric to record the count of workers in shutdown list
- [CELEBORN-1322] Rename LostWorkers metric to LostWorkerCount to align the naming style
- [CELEBORN-1321] Change noisy expire shuffle log to debug level and aggregate log
- [CELEBORN-1320] Use ReviveManager for soft splits
- [CELEBORN-1317][FOLLOWUP] Update default value of celeborn.master.http.maxWorkerThreads and celeborn.worker.http.maxWorkerThreads via QueuedThreadPool
- [CELEBORN-1317][FOLLOWUP] ServerConnector supports celeborn.master.http.stopTimeout and celeborn.worker.http.stopTimeout
- [CELEBORN-1317][FOLLOWUP] HttpServer avoid Jetty's acceptor thread shrink for stopping
- [CELEBORN-1317] Refine celeborn http server and support swagger ui
- [CELEBORN-1316] Override toString method for StoreVersion
- [CELEBORN-1315] Manually close the RocksDB/LevelDB instance when checkVersion throw Exception
- [CELEBORN-1314] add capacity-bounded inbox for rpc endpoint
- [CELEBORN-1313] Custom Network Location Aware Replication
- [CELEBORN-1312] Move handleRequestPartitions out of sync block
- [CELEBORN-1310][FOLLOWUP] License check add flink-1.19 profile
- [CELEBORN-1310][FLINK] Support Flink 1.19
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
- [CELEBORN-1301] Catch and throw FetchFailedException in CelebornInputStream#fillBuffer
- [CELEBORN-1300] Optimize CelebornInputStreamImpl's memory usage
- [CELEBORN-1299] Introduce JVM profiling in Celeborn Worker using async-profiler
- [CELEBORN-1298] Support Spark2.4 with Scala2.12
- [CELEBORN-1297] Change DB script column from user to name
- [CELEBORN-1296] Introduce celeborn.dynamicConfig.store.fs.path config to configure the path of dynamic config file for fs store backend
- [CELEBORN-1295][FOLLOWUP] Change repo_url to apache repo
- [CELEBORN-1295] Add tm to Celeborn's website
- [CELEBORN-1293] Output received signals at master and worker
- [CELEBORN-1292] Remove app level metrics from worker and master
- [CELEBORN-1291] Master crashed causing by huge app level worker consumption info
- [CELEBORN-1288] Prompt configuration items when receiving IdleStateEvent
- [CELEBORN-1287] Improve both combine and sort operation of shuffle read for CelebornShuffleReader
- [CELEBRON-1285] Add check tenantConfig.getConfigs().isEmpty() in getTenantUserConfigFromCache
- [CELEBRON-1282] Optimize push data replica error message
- [CELEBORN-1277] Add celeborn.quota.enabled at Master and Client side to enable checking quota
- [CELEBORN-1276] Move checkQuotaSpaceAvailable from Quota to QuotaManager
- [CELEBORN-1273] Move java classes under scala src to java
- [CELEBORN-1267] Add config to control worker check in CelebornShuffleFallbackPolicyRunner
- [CELEBORN-1266] Improve log of current failed workers for WorkerStatusTracker
- [CELEBORN-1265][FOLLOWUP] Remove unnecessary GlutenShuffleDependencyHelper
- [CELEBORN-1265] Fix batches read metric for gluten columnar shuffle
- [CELEBORN-1264] ConfigService supports TENANT_USER config level
- [CELEBORN-1261] Add auth support to client
- [CELEBORN-1260] Improve Spark Configuration of Deploy Spark client for deployment document
- [CELEBORN-1259] Improve the default gracePeriod of ThreadUtils#shutdown
- [CELEBORN-1257][FOLLOWUP] Removed the additional secured port from Celeborn Master
- [CELEBORN-1257] Adds a secured port in Celeborn Master for secure communication with LifecycleManager
- [CELEBORN-1256] Added internal port and auth support to Celeborn worker
- [CELEBORN-1254][FOLLOWUP] Rename celeborn.worker.sortPartition.reservedMemory.enabled to celeborn.worker.sortPartition.prefetch.enabled
- [CELEBORN-1254] PartitionFilesSorter seeks to position of each block and does not warm up for non-hdfs files
- [CELEBORN-1253] Improve exception message of fetching chunk failure for WorkerPartitionReader
- [CELEBORN-1251] Connect the server and client bootstraps to RpcEnv
- [CELEBORN-1249] Add LICENSE of Celeborn Web
- [CELEBORN-1246] Introduce OpenStreamSuccessCount, FetchChunkSuccessCount and WriteDataSuccessCount metric to expose the count of opening stream, fetching chunk and writing data successfully
- [CELEBORN-1245][FOLLOWUP] Fix SendWorkerEvent in HA mode
- [CELEBORN-1245] Support Celeborn Master(Leader) to manage workers
- [CELEBORN-1244] Delete redundant remove operations and handle timeout requests in final check
- [CELEBORN-1242] Unify celeborn thread name format
- [CELEBORN-1241][FOLLOWUP] Fix duplicate CelebornRackResolver issue for SingleMasterMetaManager
- [CELEBORN-1241] Introduce hot load for CelebornRackResolver
- [CELEBORN-1240][FOLLOWUP] Web lint check uses different groups
- [CELEBORN-1240] Introduce Husky Configuration to Celeborn Web
- [CELEBORN-1239][FOLLOWUP] Deprecate celeborn.quota.configuration.path config
- [CELEBORN-1239] Celeborn QuotaManager support use ConfigService and support default quota setting
- [CELEBORN-1238] deviceCheckThreadPool is only initialized when diskCheck is enabled
- [CELEBORN-1237] Refactor metrics name
- [CELEBORN-1236][FOLLOWUP] Gauge is_terminating, is_terminated and is_shutdown should represent a single numerical value
- [CELEBORN-1236] Celeborn add metrics about thread pool
- [CELEBORN-1235] Start test nodes in random ports to allow multiple builds run in the same ci server
- [CELEBORN-1234] Master should persist the application meta in Ratis and push it to the Workers
- [CELEBORN-1232] Add Menu to Celeborn Web
- [CELEBORN-1231] Support baseline implementation of Celeborn Web
- [CELEBORN-1230] Check working directory read and write error without init delay
- [CELEBORN-1229] Support for application registration with Celeborn Master
- [CELEBORN-1228] Format the timestamp when recording worker failure
- [CELEBORN-1226] Unify creation of thread using ThreadUtils
- [CELEBORN-1224] Make TransportMessage#type transient for backward compatibility
- [CELEBORN-1220] Make trim logic more robust
- [CELEBORN-1219] takeBuffer() avoid checking source.metricsCollectCriticalEnabled twice
- [CELEBORN-1218] Optimize dataPusher to get partitionLocationMap only once
- [CELEBORN-1217] Improve exception message of loadFileGroup for ShuffleClientImpl
- [CELEBORN-1215] Introduce PausePushDataAndReplicateTime metric to record time for a worker to stop receiving pushData from clients and other workers
- [CELEBORN-1214] Introduce WriteDataHardSplitCount metric to record HARD_SPLIT partitions of PushData and PushMergedData
- [CELEBORN-1213] Add pronunciation of Celeborn in README.md
- [CELEBORN-1212] Support for Anonymous SASL Mechanism
- [CELEBORN-1211] Add extension for celeborn shuffle handler
- [CELEBORN-1210] Fix potential memory leak in PartitionFilesCleaner
- [CELEBORN-1209] Print Warning Log if User use Celeborn with enabled Spark ShuffleTracking
- [CELEBORN-1208] Unify parse uniqueId to WorkerInfo
- [CELEBORN-1201] Optimize memory usage of cache in partition sorter
- [CELEBORN-1197] Avoid using the sleep command with the s suffix in bash scripts
- [CELEBORN-1195] Use batch rack resolve when restore meta from file
- [CELEBORN-1190] Apply error prone patch and suppress some problems
- [CELEBORN-1189] Introduce RunningApplicationCount metric and /applications API to record running applications of worker
- [CELEBORN-1188] Using JUnit function instead of java assert
- [CELEBORN-1187] Unify the size and file count of active shuffle metrics for master and worker
- [CELEBORN-1184] Update the snakeyaml version from 1.33 to 2.2
- [CELEBORN-1182][FOLLOWUP] Worker should remove application active connection via clean thread pool
- [CELEBORN-1182] Support application dimension ActiveConnectionCount metric to record the number of registered connections for each application
- [CELEBORN-1179] Add support in Celeborn Workers to fetch application meta from the Master
- [CELEBORN-1174][FOLLOWUP] Master computeResourceConsumption miss applicationId
- [CELEBORN-1174] Introduce application dimension resource consumption metrics
- [CELEBORN-1172] Support dynamic switch shuffle push write mode based on partition number
- [CELEBORN-1144] Batch OpenStream RPCs
- [CELEBORN-1133] Refactor fileinfo
- [CELEBORN-1100] Introduce ChunkStreamCount, OpenStreamFailCount metrics about opening stream of FetchHandler
- [CELEBORN-1078] Log info to indicate columnar shuffle writer take effect
- [CELEBORN-1054] Support db based dynamic config service
- [CELEBORN-1052][FOLLOWUP] Improve the implementation of ConfigService
- [CELEBORN-1051] Add isDynamic property for CelebornConf
- [CELEBORN-1012] Add a dedicated internal port in Master to talk to Workers and other Masters
- [CELEBORN-914] Support memory file storage
- [CELEBORN-891] Remove pipeline feature for sort based writer
- [CELEBORN-863][FOLLOWUP] Fix persisted committed file infos lost
- [CELEBORN-448][FOLLOWUP] HeartbeatFromApplicationResponse should include manually excluded workers
- [MINOR] Update log level of PushData and PushMergedData for PushDataHandler from info to debug
- [MINOR] Update log level of CommitFiles success for CommitHandler from error to info
- [MINOR] Unifiy license format of pom.xml
- [MINOR] Improve SuiteJ of client-flink module
- [MINOR] Fix typos
- [MINOR] Fix typo in TransportClient
- [INFRA][FOLLOWUP] Fix copyright of mkdocs.yml for graduation
- [INFRA] Remove incubator/incubating for graduation


### Stability and Bug Fix
- [CELEBORN-1462] Fix layout of DeviceCelebornTotalBytes, DeviceCelebornFreeBytes, RunningApplicationCount and DecommissionWorkerCount in celeborn-dashboard.json
- [CELEBORN-1457] Avoid NPE during shuffle data cleanup
- [CELEBORN-1456] Fix LICENSE dependencies in LICENSE-binary
- [CELEBORN-1456][FOLLOWUP] Fix license issue
- [CELEBORN-1452] Master follower node metadata is out of sync after installing snapshot
- [CELEBORN-1450] MRAppMasterWithCeleborn should get FileSystem via mapreduce.job.dir for HDFS federation
- [CELEBORN-1449] Fix JavaUtils#deleteRecursivelyUsingJavaIO to skip non-existing file input
- [CELEBORN-1432] ShuffleClientImpl should invoke loadFileGroupInternal only once when using the reduce partition mode
- [CELEBORN-1424] Fix getChunk NPE when enable local read
- [CELEBORN-1420] Fix mapreduce job will throw an exit exception after it succeeded
- [CELEBORN-1399] MR CelebornMapOutputCollector should check exception after flush
- [CELEBORN-1393][HELM] Resource labels and selector labels are duplicated
- [CELEBORN-1391] Retry when MasterClient receiving a RpcTimeoutException
- [CELEBORN-1380] leveldbjni uses org.openlabtesting.leveldbjni to support linux aarch64 platform for leveldb
- [CELEBORN-1317][FOLLOWUP] Fix threadDump UT stuck issue
- [CELEBORN-1317][FOLLOWUP] Improve parameters, description and document of REST API
- [CELEBORN-1317][FOLLOWUP] Retry to setup mini cluster if the cause isBindException
- [CELEBORN-1290] Fix NPE occurring prior to worker registration
- [CELEBORN-1283] TransportClientFactory avoid contention and get or create clientPools quickly
- [CELEBORN-1282][FOLLOWUP] Fix FetchHandler#handleEndStreamFromClient NullPointerException after recycling stream of CreditStreamManager
- [CELEBORN-1282][FOLLOWUP] Introduce ReplicateDataFailNonCriticalCauseCount metric in Grafana dashboard
- [CELEBORN-1280] Change default value of celeborn.worker.graceful.shutdown.recoverDbBackend to ROCKSDB
- [CELEBORN-1278] Avoid calculating all outstanding requests to improve performance
- [CELEBORN-1275] Fix bug that callback function may hang when unchecked exception missed
- [CELEBORN-1272] Do not increment epoch when retry commit
- [CELEBORN-1271] Fix unregisterShuffle with celeborn.client.spark.fetch.throwsFetchFailure disabled
- [CELEBORN-1270] Introduce PbPackedPartitionLocations to (de-)serialize PartitionLocations more efficiently
- [CELEBORN-1255] Fix MR UT
- [CELEBORN-1252] Fix resource consumption of worker does not update when update interval is greater than heartbeat interval
- [CELEBORN-1233] Treat unfound PartitionLocation as failed in Controller#commitFiles
- [CELEBORN-1225] Worker should build replicate factory to get client for sending replicate data
- [CELEBORN-1222] Fix Celeborn worker won't record HDFS writer
- [CELEBORN-1216] Resolve error occurring during distribution creation with profile `-Pspark-2.4`
- [CELEBORN-1196] Slots allocator will increment disk index repeatedly
- [CELEBORN-1193] ResettableSlidingWindowReservoir should reset full to false
- [CELEBORN-1192] Celeborn wait task timeout error message should show correct corresponding batch and target host and port
- [CELEBORN-1182][FOLLOWUP] Fix WorkerSource record application active connection for application dimension ActiveConnectionCount metric
- [CELEBORN-1182][FOLLOWUP] WorkerSource should use Counter to support application dimension ActiveConnectionCount metric
- [CELEBORN-1177] OpenStream should register stream via ChunkStreamManager to close stream for ReusedExchange
- [CELEBORN-1056] Introduce Rest API of listing dynamic configuration
- [CELEBORN-1036] totalInflightReqs should decrement when batchIdSet contains the batchId to avoid duplicate caller of removeBatch
- [CELEBORN-1016] Fix IPv6 host address resolve issue
- [CELEBORN-891] Remove pipeline feature for sort based writer
- [CELEBORN-775] Fix executorCores calculation in SparkShuffleManager for Spark2 local mode
- [MINOR] Fix config type of imagePullSecrets in values.yaml for helm
- [MINOR] Fix typos and wrong package name
- [MINOR] Fix typos in profile name when checking dependencies

### Build
- [CELEBORN-1438] Exclude celeborn-service_xx-test ja
- [CELEBORN-1405][BUILD] SBT allows using credential without a realm
- [CELEBORN-1404][BUILD] Disable SBT ANSI color on extracting info from output
- [CELEBORN-1331] Remove third-party dependencies in shaded clients' pom
- [CELEBORN-1263] Fix Master HA mode without internal port error
- [CELEBORN-1250] Fix license issues
- [CELEBORN-1207] SBT http repository documentation
- [CELEBORN-1205] Disable Maven local caches to improve SBT building stability
- [CELEBORN-1204] Update NOTICE year 2024
- [CELEBORN-1203] Add LicenseAndNoticeMergeStrategy to resolve inner project LICENSE/NOTICE conflict for shaded client packaging
- [CELEBORN-1202] Update LICENSE and NOTICE files
- [CELEBORN-1202] LICENSE mentions third-party components under other open source licenses
- [CELEBORN-1199] Disabled the plugin `AddMetaInfLicenseFiles` for shaded clients
- [CELEBORN-1199] Add LICENSE and NOTICE files for service related sub-projects
- [CELEBORN-1198] Keep debug info when use SBT build
- [CELEBORN-1194] Add sbt-pgp plugin for publishing signed artifacts
- [CELEBORN-1191] Migrate the release script from Maven to SBT
- [CELEBORN-1006][FOLLOWUP] Dependency hadoop-client should exclude hadoop-mapreduce-client dependencies for Hadoop 2
- [MINOR] Remove incubator in vcs.xml

### Documentation
- [CELEBORN-1369][FOLLOWUP] Improve docs for shuffle fallback policy
- [CELEBORN-1353] Document Celeborn security - authentication and SSL support
- [CELEBORN-1341][FOLLOWUP] Improve Celeborn document
- [CELEBORN-1317][FOLLOWUP] Remove Incubating from REST API Documentation
- [CELEBORN-1311] Developers Doc introduce Slots allocation
- [CELEBORN-1286] Introduce configuration.md to document dynamic config and config service
- [CELEBORN-1284][FOLLOWUP] Fix license style of quota_management.md
- [CELEBORN-1284][DOC] Add document about QuotaManager based on ConfigService
- [CELEBORN-1247] Output config's alternatives to doc
- [CELEBORN-1223] Align master and worker metrics of document with MasterSource and WorkerSource
- [CELEBORN-1134][FOLLOWUP] Add execution.batch-shuffle-mode: ALL_EXCHANGES_BLOCKING to Flink Configuration of Deploy Flink client
- [MINOR] Fix typo of celeborn.network.bind.preferIpAddress doc
- [MINOR] Fix typo in developer docs - overview
- [MINOR] Fix style and Gluten link in Developers Doc

### Dependencies
- [CELEBORN-1400] Bump Ratis version from 2.5.1 to 3.0.1
- [CELEBORN-1396] Bump Netty from 4.1.107.Final to 4.1.109.Final
- [CELEBORN-1395] Bump RoaringBitmap version from 1.0.5 to 1.0.6
- [CELEBORN-1394] Bump Spark from 3.4.2 to 3.4.3
- [CELEBORN-1389] Bump Dropwizard version from 3.2.6 to 4.2.25
- [CELEBORN-1382] Bump RoaringBitmap version from 0.9.32 to 1.0.5
- [CELEBORN-1366] Bump guava from 32.1.3-jre to 33.1.0-jre
- [CELEBORN-1330] Bump rocksdbjni version from 8.5.3 to 8.11.3
- [CELEBORN-1281] Bump Spark from 3.5.0 to 3.5.1
- [CELEBORN-1262] Bump Spark from 3.3.3 to 3.3.4
- [CELEBORN-1243] Bump Flink from 1.18.0 to 1.18.1
- [CELEBORN-1221] Bump Flink from 1.17.0 to 1.17.2
- [MINOR] Bump minikube and kubernetes version of integration test
- [BUILD] Bump netty version to latest 4.1.107.Final

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.4.1 version:

| Contributors |                 |                   |                 |                     |               |
|--------------|-----------------|-------------------|-----------------|---------------------|---------------|
| albin3       | AngersZhuuuu    | Aravind Patnam    | binjie yang     | cfmcgrady           | ChenYi015     |
| CodingCat    | Curtis Howard   | cxzl25            | dev-lpq         | ErikFang            | FMX           |
| ForVic       | huangxiaopingRD | jiang13021        | jiaoqingbo      | kerwin-zk           | labbomb       |
| leixm        | lifulong        | lyy-pineapple     | miyuesc         | Mridul Muralidharan | mridulm       |
| onebox-li    | otterc          | pan3793           | radeity         | s0nskar             | SteNicholas   |
| tiny-dust    |   turboFei      |      waitinfuture |       wxplovecc |   xianminglei       |    xinyuwang1 |
| zwangsheng   |                 |                   |                 |                     |               |
