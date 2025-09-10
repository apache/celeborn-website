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

# Apache Celeborn™ 0.6.1 Release Notes

## Highlight

- Support to register application info with user identifier and extra info
- Support celeborn.client.push.maxBytesSizeInFlight
- Fix the issue where reading replica partition that returns zero chunk causes tasks to hang

### Improvement

- [CELEBORN-1258] Support to register application info with user identifier and extra info
- [CELEBORN-1793] Add netty pinned memory metrics
- [CELEBORN-1844][FOLLOWUP] alway try to use memory storage if available
- [CELEBORN-1917] Support celeborn.client.push.maxBytesSizeInFlight
- [CELEBORN-2044] Proactively cleanup stream state from ChunkStreamManager when the stream ends
- [CELEBORN-2056] Make the wait time for the client to read non shuffle partitions configurable
- [CELEBORN-2061] Introduce metrics to count the amount of data flushed into different storage types
- [CELEBORN-2070][CIP-14] Support MapperEnd/Response in CppClient
- [CELEBORN-2072] Add missing instance filter to grafana dashboard
- [CELEBORN-2077] Improve toString by JEP-280 instead of ToStringBuilder
- [CELEBORN-2081] PushDataHandler onFailure log shuffle key
- [CELEBORN-2082] Add the log of excluded workers with high workloads
- [CELEBORN-2083] For `WorkerStatusTracker`, log error for `recordWorkerFailure`
- [CELEBORN-2085] Use a fixed buffer for flush copying to reduce GC
- [CELEBORN-2090] Support Lz4 Decompression in CppClient
- [CELEBORN-2092] Inc COMMIT_FILES_FAIL_COUNT when TimerWriter::close timeout
- [CELEBORN-2102] Introduce SorterCacheHitRate metric to monitor the hit reate of index cache for sorter
- [CELEBORN-2104] Clean up sources of NettyRpcEnv, Master and Worker to avoid thread leaks
- [CELEBORN-2106] CommitFile/Reserved location shows detail primary location UniqueId
- [CELEBORN-2108] Remove redundant PartitionType
- [CELEBORN-2112] Introduce PausePushDataStatus and PausePushDataAndReplicateStatus metric to record status of pause push data
- [CELEBORN-2117] Use git submodules for Chart Actions
- [CELEBORN-2118] Introduce IsHighWorkload metric to monitor worker overload status
- [CELEBORN-2122] Avoiding multiple accesses to HDFS when retrieving indexes in DfsPartitionReader
- [CELEBORN-2123] Add log for commit file size
- [CELEBORN-2125] Improve PartitionFilesSorter sort timeout log
- [CELEBORN-2128] Close hadoopFs FileSystem when worker is closed
- [CELEBORN-2129] CelebornBufferStream should invoke openStreamInternal in moveToNextPartitionIfPossible to avoid client creation timeout
- [CELEBORN-2133] LifecycleManager should log stack trace of Throwable for invoking appShuffleTrackerCallback

### Stability and Bug Fix

- [CELEBORN-1792][FOLLOWUP] Add missing break in resumeByPinnedMemory
- [CELEBORN-1844][FOLLOWUP] Fix the condition of StoragePolicy that worker uses memory storage
- [CELEBORN-2052] Fix unexpected warning logs in Flink caused by duplicate BufferStreamEnd messages
- [CELEBORN-2064] Fix the issue where reading replica partition that returns zero chunk causes tasks to hang
- [CELEBORN-2068] TransportClientFactory should close channel explicitly to avoid resource leak for timeout or failure
- [CELEBORN-2071] Fix the issue where some gauge metrics were not registered to the metricRegistry
- [CELEBORN-2073] Fix PartitionFileSizeBytes metrics
- [CELEBORN-2075] Fix `OpenStreamTime` metrics for `PbOpenStreamList` request
- [CELEBORN-2078] Fix wrong grafana metrics units
- [CELEBORN-2086] S3FlushTask and OssFlushTask should close ByteArrayInputStream to avoid resource leak
- [CELEBORN-2088] Fix NPE if `celeborn.client.spark.fetch.cleanFailedShuffle` enabled
- [CELEBORN-2100] Fix performance issue on readToReadOnlyBuffer
- [CELEBORN-2119] DfsTierWriter should close s3MultipartUploadHandler and ossMultipartUploadHandler for close resource
- [CELEBORN-2139] Fix the condition for using OSS storage

### Documentation

- [CELEBORN-2135] Rename Blaze to Auron
- [CELEBORN-2087] Refine the docs configuration table view

### Dependencies

- [CELEBORN-2080] Bump Flink from 1.19.2, 1.20.1 to 1.19.3, 1.20.2

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.6.1 version:

| Contributors |               |             |                     |                |          |
|--------------|---------------|-------------|---------------------|----------------|----------|
| Ethan Feng   | Hao Duan      | Jiaming Xie | Mridul Muralidharan | Nicholas Jiang | Rui Zhuo |
| Shaoyun Chen | Wang Fei      | Xian Zhuang | Xinyu Wang          | Xu Huang       | Yang Liu |
| Zhaohui Xu   | Zhengqi Zhang |             |                     |                |          |
