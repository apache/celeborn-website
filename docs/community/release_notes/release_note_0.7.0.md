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

# Apache Celeborn™ 0.7.0 Release Notes

## Highlight

- **Engine Support**: Spark 4.1/4.2 and Flink 2.1/2.2/2.3 support; Spark 4.0 and Flink 2.0 client maturity. Removed Spark 2.4, Flink 1.16/1.17, and Hadoop 2 client support.
- **C++ Native Client (CIP-14)**: Full push/merge data path including PushData and PushMergedData, revive and ReviveManager, MapperEnd support, LZ4/ZSTD compression and decompression, retry logic for createReader and RetryFetchChunk, heartbeat message decoding, native transport with DNS resolution, and C++-write/Java-read hybrid integration tests for LZ4 and ZSTD — a major push toward feature parity with the Java client.
- **Encryption at Rest (CIP-22)**: Spark-side implementation landed.
- **Shuffle Integrity**: End-to-end integrity check for Flink, iterator fully-consumed validation, and extended E2E checked zone.
- **Remote Storage (S3/OSS/HDFS)**: S3 client caching, credentials provider support, DFS replication factor configuration, MapPartitionData on DFS, and multiple robustness fixes.
- **Observability**: Flush latency per storage tier, eviction counts, read buffer availability, metadata operation metrics, and more — all reflected in the Grafana dashboards.
- **Performance**: Zero-copy sendfile for FileRegion in Netty native transports, parallelized open stream and commit paths, reduced lock contention on hot paths, and parallel pushData/mergeData in hash writer close phase.
- **Durability & Reliability**: fsync on commit, automatic RocksDB restoration, disk-full rejection, Ratis stepdown and outbox retry, and numerous race-condition and resource-leak fixes.
- **Security**: RPC authorization for ReviseLostShuffles and ApplicationMetaRequest to prevent cross-application metadata modification and secret disclosure, Java deserialization filter, configurable HTTP auth bypass paths, HTTP authentication for Celeborn CLI, and CVE fixes for lz4-java, jersey, and jetty.
- **Standalone LifecycleManager and Rust SDK** for running Celeborn's shuffle lifecycle management without a Spark/Flink driver, plus Helm chart improvements including volume claim templates, service account annotations, and configurable worker pod grace period.

### Improvement

- [CELEBORN-2401] Change default of celeborn.client.spark.fetch.cleanFailedShuffle to true
- [CELEBORN-2399] Fix HA worker heartbeat using stateStartTime instead of actual time
- [CELEBORN-2406] Avoid blocking GetReducerFileGroup RPC under ConcurrentHashMap bin lock in updateFileGroup
- [CELEBORN-2402] Log the hostname for Celeborn shuffle fetch failures
- [CELEBORN-2404] Fix required field typo in SendWorkerEventRequest schema
- [CELEBORN-2394] Add REST v1 API to unregister shuffles
- [CELEBORN-2396][CLI] Fix CelebornCli.main discarding the configured CommandLine instance
- [CELEBORN-2395] Reduce allocations in Utils.split* methods
- [CELEBORN-2397][CLI] Expose logger level get/set endpoints in celeborn-cli
- [CELEBORN-2308] Bump commons-lang3 version from 3.17.0 to 3.20.0

- [CELEBORN-894] End to End Integrity Checks
- [CELEBORN-935][FOLLOWUP] Remove WorkerRemove in MetaHandler
- [CELEBORN-1056][FOLLOWUP] Support upsert and delete of dynamic configuration management
- [CELEBORN-1258] Support to register application info with user identifier and extra info
- [CELEBORN-1319][FOLLOWUP] Support celeborn optimize skew partitions patch for Spark v3.5.6 and v4.0.0
- [CELEBORN-1413][FOLLOWUP] Bump spark 4.0 version to 4.0.0
- [CELEBORN-1528][HELM] Use volume claim template to support various storage backend
- [CELEBORN-1572][FOLLOWUP] Support to show Celeborn CLI version for sub command
- [CELEBORN-1577][FOLLOWUP] Improve check quota message
- [CELEBORN-1627][FOLLOWUP] Fix the issue where the case of name affects the metrics dashboard
- [CELEBORN-1673][FOLLOWUP] Shouldn't ignore InterruptedException when client retry
- [CELEBORN-1719][FOLLOWUP] Rename throwsFetchFailure to stageRerunEnabled
- [CELEBORN-1720][FOLLOWUP] Fix flakyTest - check if fetch failure task another attempt is running or successful
- [CELEBORN-1721][FOLLOWUP] Return softsplit if there is no hardsplit for pushMergeData
- [CELEBORN-1792][FOLLOWUP] Add missing break in resumeByPinnedMemory
- [CELEBORN-1793] Add netty pinned memory metrics
- [CELEBORN-1817][FOLLOWUP] Correct the problematic metrics
- [CELEBORN-1844][FOLLOWUP] alway try to use memory storage if available
- [CELEBORN-1892] Adding register with master fail count metric for worker
- [CELEBORN-1917] Support celeborn.client.push.maxBytesSizeInFlight
- [CELEBORN-1931][FOLLOWUP] Update config version for worker local flusher gather api
- [CELEBORN-1983][FOLLOWUP] Fix fetch fail not throw due to reach spark maxTaskFailures
- [CELEBORN-1984] Merge ResourceRequest to transportMessageProtobuf
- [CELEBORN-2003] Add retry mechanism when completing S3 multipart upload
- [CELEBORN-2005] Introduce numBytesIn, numBytesOut, numBytesInPerSecond, numBytesOutPerSecond metrics for RemoteShuffleServiceFactory
- [CELEBORN-2006] LifecycleManager should avoid parsing shufflePartitionType every time
- [CELEBORN-2007] Reduce PartitionLocation memory usage
- [CELEBORN-2008] SlotsAllocator should select disks randomly in RoundRobin mode
- [CELEBORN-2009] Commit files request failure should exclude worker in LifecycleManager
- [CELEBORN-2010][INFRA] Add release guide
- [CELEBORN-2011][INFRA] Add a script to simplify the process of creating release notes
- [CELEBORN-2012] Add license for http5
- [CELEBORN-2013] Upgrade scala binary version of spark-3.3, spark-3.4, spark-3.5 profile to 2.13.8
- [CELEBORN-2014] updateInterruptionNotice REST API
- [CELEBORN-2015] Retry IOException failures for RPC requests
- [CELEBORN-2017][HELM] Add namespace to the metadata
- [CELEBORN-2018] Support min number of workers selected for shuffle
- [CELEBORN-2020] Support http authentication for Celeborn CLI
- [CELEBORN-2021] Fix issues on regression HDFS and OSS before release 0.6
- [CELEBORN-2022] Spark4 Client should package commons-io
- [CELEBORN-2023] Spark4 Client incompatible with isLocalMaster method
- [CELEBORN-2024] Publish commit files fail count metrics
- [CELEBORN-2025] RpcFailure Scala 2.13 serialization is incompatible
- [CELEBORN-2026] Skip build tez client
- [CELEBORN-2027] Allow CelebornShuffleReader to decompress data on demand
- [CELEBORN-2028] Setup GA for grafana dashboard
- [CELEBORN-2029][FLINK] Some minor optimizations in the Flink integration
- [CELEBORN-2030] Bump Spark from 3.5.5 to 3.5.6
- [CELEBORN-2031] Interruption Aware Slot Selection
- [CELEBORN-2032] Create reader should change to peer by taskAttemptId
- [CELEBORN-2033] updateProduceBytes should be called even if updateProduceBytes throws exception
- [CELEBORN-2036] Fix NPE when TransportMessage has null payload
- [CELEBORN-2040] Avoid throw FetchFailedException when GetReducerFileGroupResponse failed via broadcast
- [CELEBORN-2042] Fix FetchFailure handling when TaskSetManager is not found
- [CELEBORN-2043] Fix IndexOutOfBoundsException exception in getEvictedFileWriter
- [CELEBORN-2044] Proactively cleanup stream state from ChunkStreamManager when the stream ends
- [CELEBORN-2045] Add logger sinks to allow persist metrics data and avoid possible worker OOM
- [CELEBORN-2046] Specify extractionDir of AsyncProfilerLoader with celeborn.worker.jvmProfiler.localDir
- [CELEBORN-2047] Support MapPartitionData on DFS
- [CELEBORN-2049] Bump Ratis version from 3.1.3 to 3.2.1
- [CELEBORN-2051] Support write MapPartition to DFS
- [CELEBORN-2052] Fix unexpected warning logs in Flink caused by duplicate BufferStreamEnd messages
- [CELEBORN-2053] Refactor remote storage configration usage
- [CELEBORN-2055] Fix some typos
- [CELEBORN-2056] Make the wait time for the client to read non shuffle partitions configurable
- [CELEBORN-2057] Bump ap-loader version from 3.0-9 to 4.0-10
- [CELEBORN-2058] Add retry to avoid committing failed with HDFS storage
- [CELEBORN-2061] Introduce metrics to count the amount of data flushed into different storage types
- [CELEBORN-2063] Parallelize the create partition writer in handleReserveSlots to speed up the reserveSlots RPC process time
- [CELEBORN-2064] Fix the issue where reading replica partition that returns zero chunk causes tasks to hang
- [CELEBORN-2066] Release workers only with high workload when the number of excluded worker set is too large
- [CELEBORN-2067] Clean up deprecated Guava API usage
- [CELEBORN-2068] TransportClientFactory should close channel explicitly to avoid resource leak for timeout or failure
- [CELEBORN-2070][CIP-14] Support MapperEnd/Response in CppClient
- [CELEBORN-2071] Fix the issue where some gauge metrics were not registered to the metricRegistry
- [CELEBORN-2072] Add missing instance filter to grafana dashboard
- [CELEBORN-2073] Fix PartitionFileSizeBytes metrics
- [CELEBORN-2075] Fix OpenStreamTime metrics for PbOpenStreamList request
- [CELEBORN-2077] Improve toString by JEP-280 instead of ToStringBuilder
- [CELEBORN-2078] Fix wrong grafana metrics units
- [CELEBORN-2080] Bump Flink from 1.19.2, 1.20.1 to 1.19.3, 1.20.2
- [CELEBORN-2081] PushDataHandler onFailure log shuffle key
- [CELEBORN-2082] Add the log of excluded workers with high workloads
- [CELEBORN-2083] For WorkerStatusTracker, log error for recordWorkerFailure
- [CELEBORN-2085] Use a fixed buffer for flush copying to reduce GC
- [CELEBORN-2086] S3FlushTask and OssFlushTask should close ByteArrayInputStream to avoid resource leak
- [CELEBORN-2087] Refine the docs configuration table view
- [CELEBORN-2088] Fix NPE if celeborn.client.spark.fetch.cleanFailedShuffle enabled
- [CELEBORN-2090] Support Lz4 Decompression in CppClient
- [CELEBORN-2091] Support Zstd Decompression in CppClient
- [CELEBORN-2092] Inc COMMIT_FILES_FAIL_COUNT when TimerWriter::close timeout
- [CELEBORN-2093] Support Flink 2.1
- [CELEBORN-2095][CIP-14] Support RegisterShuffle/Response in cppClient
- [CELEBORN-2096] Support Lz4 Compression in CppClient
- [CELEBORN-2097] Support Zstd Compression in CppClient
- [CELEBORN-2098][CIP-14] Support Revive/Response in cppClient
- [CELEBORN-2100] Fix performance issue on readToReadOnlyBuffer
- [CELEBORN-2102] Introduce SorterCacheHitRate metric to monitor the hit rate of index cache for sorter
- [CELEBORN-2104] Clean up sources of NettyRpcEnv, Master and Worker to avoid thread leaks
- [CELEBORN-2105] RpcMetricsTracker should clean up metrics for stopping Inbox
- [CELEBORN-2106] CommitFile/Reserved location shows detail primary location UniqueId
- [CELEBORN-2108] Remove redundant PartitionType
- [CELEBORN-2109] Add EvictedLocalFileCount and EvictedDfsFileCount metrics
- [CELEBORN-2111] Introduce metrics to flush time for different storage types
- [CELEBORN-2112] Introduce PausePushDataStatus and PausePushDataAndReplicateStatus metric to record status of pause push data
- [CELEBORN-2115][CIP-14] Support PushData in cppClient
- [CELEBORN-2117] Use git submodules for Chart Actions
- [CELEBORN-2118] Introduce IsHighWorkload metric to monitor worker overload status
- [CELEBORN-2119] DfsTierWriter should close s3MultipartUploadHandler and ossMultipartUploadHandler for close resource
- [CELEBORN-2122] Avoiding multiple accesses to HDFS when retrieving index
- [CELEBORN-2123] Add log for commit file size
- [CELEBORN-2124][CIP-14] Support PushData network interfaces in cppClient
- [CELEBORN-2125] Improve PartitionFilesSorter sort timeout log
- [CELEBORN-2127] When fileWriter is closed, it should return HARD_SPLIT StatusCode
- [CELEBORN-2128] Close hadoopFs FileSystem when worker is closed
- [CELEBORN-2129] CelebornBufferStream should invoke openStreamInternal in moveToNextPartitionIfPossible to avoid client creation timeout
- [CELEBORN-2130] Add support for redirection application registration to the leader
- [CELEBORN-2131] Add sorting duration logs in FileSorter
- [CELEBORN-2132] Enhance ratis peer add operation to support clientAddress & adminAddress
- [CELEBORN-2133] LifecycleManager should log stack trace of Throwable for invoking appShuffleTrackerCallback
- [CELEBORN-2134] When creating a DiskFile, retrieve the storage type
- [CELEBORN-2135] Rename Blaze to Auron
- [CELEBORN-2137] Remove unused MAPGROUP PartitionType
- [CELEBORN-2138] Avoiding multiple accesses to HDFS When writing index file
- [CELEBORN-2139] Fix the condition for using OSS storage
- [CELEBORN-2142] DfsTierWriter should create for unavailable disks
- [CELEBORN-2144] Bump spark 4.0.1
- [CELEBORN-2145] QuotaManager should respect celeborn.quota.interruptShuffle.enabled of client config
- [CELEBORN-2146] Setting the DFS replication factor for balanced fault tolerance and storage efficiency
- [CELEBORN-2149] Remove the deprecated ReleaseSlots
- [CELEBORN-2150] Fix the match condition in checkIfWorkingDirCleaned
- [CELEBORN-2152] Support merge buffers on the worker side to improve memory utilization
- [CELEBORN-2153] Fix NPE problem that occurs during concurrent merge
- [CELEBORN-2154] Optimize the exception handling of DFS read to avoid tasks from getting stuck
- [CELEBORN-2155] Avoid using duplicate diskFileInfoMap functions
- [CELEBORN-2157][CIP-14] Support sending RegisterShuffle/Revive/MapperEnd messages for NettyRpcEndpointRef in cppClient
- [CELEBORN-2159] Fix dfs storage type check in StorageManager#cleanupExpiredShuffleKey
- [CELEBORN-2160] Speedup CommitHandler.finishMapperAttempt
- [CELEBORN-2161] Support DB delete failure policy
- [CELEBORN-2162] Suppress client side connection error logging
- [CELEBORN-2163] PushDataHandler should increment WriteDataFailCount for file writer exception of MapPartition PushData
- [CELEBORN-2164] Fix incorrect filtering conditions in updateDiskInfos
- [CELEBORN-2165] Fix endless swagger openapi.json security items
- [CELEBORN-2166] Fast fail reduce stage if shuffle data is lost because of worker lost
- [CELEBORN-2167] Bump Spark from 3.5.6 to 3.5.7
- [CELEBORN-2168] Bump Flink from 1.20.2 to 1.20.3
- [CELEBORN-2169][CIP-14] Support ConcurrentHashMap and refactor reducerFileGroupInfos
- [CELEBORN-2170] Refactor ByteBuffer's readToReadOnlyBuffer interface
- [CELEBORN-2171] Fix array index error in submitRetryPushMergedData
- [CELEBORN-2173] jersey-test-framework-core dependency should exclude junit5 dependencies to execute java test cases for CI
- [CELEBORN-2176] Fix uncaught exception in DataPusher
- [CELEBORN-2178] Close hadoopFs FileSystem for stopping master
- [CELEBORN-2180] Fix Invalid RequestId during RegisterApplicationInfo
- [CELEBORN-2181] Modify the shuffleAllocations order in the disk info log
- [CELEBORN-2182][CIP-14] Support PushState and PushStrategy in CppClient
- [CELEBORN-2184] Introduce AvailableReadBuffer metric to monitor available memory for credit stream read buffer
- [CELEBORN-2185] Support native kqueue transport on BSD/MacOS
- [CELEBORN-2186] Add correctness related section to bug report and PR templates
- [CELEBORN-2188] Abort multipart upload for S3 and OSS in DfsTierWriter#handleException
- [CELEBORN-2189] Allow config worker pod terminationGracePeriodSeconds in chart
- [CELEBORN-2192] ReadBufferDispatcher should add timeout constraints to fast fail in case of timeout
- [CELEBORN-2193] Bump Flink from 2.0.0, 2.1.0 to 2.0.1, 2.1.1
- [CELEBORN-2194] Change default value of celeborn.worker.directMemoryRatioForReadBuffer
- [CELEBORN-2195] Align log4j2.xml and metrics.properties of charts with templates
- [CELEBORN-2196][CIP-14] Support ReviveManager in CppClient
- [CELEBORN-2198] Fix NPE in tryWithTimeoutAndCallback test due to lazy deviceCheckThreadPool not initialized
- [CELEBORN-2200] Throw IOException when compressed data header corrupted
- [CELEBORN-2202] Add configmap checksum to the statefulset annotation
- [CELEBORN-2203] Set celeborn.master.internal.endpoints in the configmap
- [CELEBORN-2205] Introduce metrics to fetch chunk for memory and local disk
- [CELEBORN-2206][CIP-14] Support PushDataCallback in CppClient
- [CELEBORN-2207] Master StatefulSet should add ratis-log4j.properties for ratis shell
- [CELEBORN-2208] Log the partition reader wait time if exceeds the threshold
- [CELEBORN-2209] Introduce ReadBufferUsageRatio metric to monitor credit stream read buffer usage
- [CELEBORN-2210] When a flushBuffer consolidation OOM exception occurs
- [CELEBORN-2211] Avoid allocating additional buffers When HdfsFlushTask writes data
- [CELEBORN-2212] Optimize the sorting efficiency of memoryWriters when evicting the largest memory file
- [CELEBORN-2214] Add extraInitContainers for worker in helm
- [CELEBORN-2217] Use a separate thread to handle RpcEndpointVerifier messages
- [CELEBORN-2218] Bump lz4-java version from 1.8.0 to 1.10.4 to resolve CVE-2025-12183 and CVE-2025-66566
- [CELEBORN-2221][CIP-14] Support writing with compression in C++ client
- [CELEBORN-2222][CIP-14] Support Retrying when createReader failed for CelebornInputStream in CppClient
- [CELEBORN-2226][CIP-14] Support RetryFetchChunk functionality for CelebornInputStream
- [CELEBORN-2229][CIP-14] Add support for celeborn.client.push.maxBytesSizeInFlight in CppClient
- [CELEBORN-2231] Upgrade jersey version to 2.47 to fix CVE-2025-12383
- [CELEBORN-2234] Bump jetty version to 9.4.58.v20250814 to fix GHSA-qh8g-58pp-2wxh
- [CELEBORN-2235][CIP-14] Adapt Java end's serialization to CppWriterClient
- [CELEBORN-2236] Avoiding regular expressions for DiskFileInfo storage type determination
- [CELEBORN-2237] Support Flink 2.2
- [CELEBORN-2238] Fix RuntimeException during stream cleanup preventing peer failover
- [CELEBORN-2239] Support Spark 4.1
- [CELEBORN-2240] Adapt to SPARK-51756 which add a new parameter checksumValue in MapStatus.apply
- [CELEBORN-2243] During the close phase of hashWriter, pushData and mergeData are sent in parallel
- [CELEBORN-2245] Bump maven 3.9.12
- [CELEBORN-2248] Implement lazy loading for columnar shuffle classes and skew shuffle method using static holder pattern
- [CELEBORN-2249] Bump Spark from 3.5.7 to 3.5.8
- [CELEBORN-2250] Fix lock contention in ReducePartitionCommitHandler.finishMapperAttempt via fine-grained locks
- [CELEBORN-2251] Introducing a shim layer and a common-tiered submodule for Flink clients
- [CELEBORN-2254] Fix support for S3 and add a simple integration test
- [CELEBORN-2256] Helm chart: add support for setting annotations on the service account (to support eks.amazonaws.com/role-arn)
- [CELEBORN-2258] Bump Netty version from 4.1.118.Final to 4.2.10.Final
- [CELEBORN-2259] The S3MultipartUploadHandler uses fs.s3a.aws.credentials.provider
- [CELEBORN-2262] Prepare S3 directory only once and cache s3 client for MultiPartUploader
- [CELEBORN-2263] Fix IndexOutOfBoundsException while reading from S3
- [CELEBORN-2265] Do not waste resources on hotpath for debug logging in HashBasedShuffleWriter and SortBasedShuffleWriter
- [CELEBORN-2266] Modernize Protobuf CMake usage and add install rules
- [CELEBORN-2267][FOLLOWUP] Add Cpp-Write Java-Read integration tests for LZ4 and ZSTD
- [CELEBORN-2268] Improve test coverage for MEMORY and S3 storage
- [CELEBORN-2269] Update Cpp TransportClient to resolve hostnames via DNS
- [CELEBORN-2270] Fix problem with eviction to tiered storage during partition split
- [CELEBORN-2271] StorageManager#saveCommittedFileInfosExecutor should call shutdown before awaitTermination
- [CELEBORN-2272] Add LZ4TPCDSDataBenchmark
- [CELEBORN-2273] Fix cache mutation in TagsManager.getTaggedWorkers()
- [CELEBORN-2278] Make HTTP auth bypass API paths configurable
- [CELEBORN-2279] Update log level from INFO to ERROR for console output in spark-it tests
- [CELEBORN-2280] Support celeborn.network.memory.allocator.type to specify netty memory allocator
- [CELEBORN-2281] Improve error logging and null checks in CreditStreamManager

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.7.0 version:

| Contributors |  |  |  |  |  |
|--------------|--------------|--------------|--------------|--------------|--------------|
| 1fanwang | afterincomparableyum | Alibaba-HZY | Amandeep Singh | Aravind Patnam | Chao Sun |
| Cheng Pan | Dzeri96 | Enrico Olivelli | ewoodbury | Fei Wang | Filip Darmanovic |
| gavin9402 | He Zhao | James Xu | jaystarshot | Jiaming Xie | Jraaay |
| Kalvin2077 | Kartikay Bhutani | Mridul Muralidharan | Nicholas Jiang | pingzh | pithecuse527 |
| pltbkd | r7raul1984 | Sanskar Modi | Saurabh Dubey | senthh | Shaoyun Chen |
| taowenjun | Venkata Krishnan Sowrirajan | wang-haihua | Yajun Gao | yew1eb | Yuriy Malygin |
| Zemin Piao | Zhaohui Xu | Zhengqi Zhang |  |  |  |
