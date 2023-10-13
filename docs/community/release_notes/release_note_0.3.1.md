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

# Apache Celeborn(Incubating) 0.3.1 Release Notes

## Highlight

- Flink supports split partitions
- Spark supports read local shuffle data
- Kubernetes deployment enhancements
- Metrics and Grafana Dashboard improvements
- Prefer to use jemalloc for memory allocation
- Globally disable thread-local cache in the shared PooledByteBufAllocator
- Fix wrongly delete running app shuffle data
- Fix occasionally data reading error when AQE is enabled

### Improvement

[CELEBORN-152] Add config to limit max workers when offering slots
[CELEBORN-468] Timeout useless lostWorkers/shutdownWorkers meta
[CELEBORN-498] Add new config for DfsPartitionReader's chunk size
[CELEBORN-627] Support split partitions
[CELEBORN-656] Should also refine log about return HARD_SPLIT in handlePushMergedData
[CELEBORN-712] Fix Utils.makeReducerKey
[CELEBORN-726] Amend method names
[CELEBORN-752] Separate local read test
[CELEBORN-752] Support read local shuffle file for spark
[CELEBORN-760] Convert OpenStream and StreamHandler to Pb
[CELEBORN-770] Convert BacklogAnnouncement, BufferStreamEnd, ReadAddCredit to PB
[CELEBORN-771] Convert PushDataHandShake, RegionFinish, RegionStart to PB
[CELEBORN-796] Support for globally disable thread-local cache in the shared PooledByteBufAllocator
[CELEBORN-798] Revert Add heartbeat from client to LifecycleManager to clean
[CELEBORN-804] ShuffleManager stop should set shuffleClient to null
[CELEBORN-807] Adjust shutdown worker logs in LifecycleManager
[CELEBORN-809] Directly use isDriver passed from SparkEnv
[CELEBORN-812] Cleanup SendBufferPool if idle for long
[CELEBORN-819] Worker close should pass close status to support handle graceful shutdown and decommission
[CELEBORN-820] Merge service shutdown and close method
[CELEBORN-827] Eliminate unnecessary chunksBeingTransferred calculation
[CELEBORN-830] Add spark integration test to verify fallback with workers unavailable
[CELEBORN-830] Check available workers in CelebornShuffleFallbackPolicyRunner
[CELEBORN-832] Support use RESTful API to trigger worker decommission
[CELEBORN-837] Add silencer plugin to suppress deprecated warnings
[CELEBORN-838] Add custom mvn flag to celeborn
[CELEBORN-846] Remove unused updateReleaseSlotsMeta in master side
[CELEBORN-847] Support use RESTful API to trigger worker exit and exitImmediately
[CELEBORN-852] Add active connection count metrics to grafana dashboard
[CELEBORN-852] Adding new metrics to record the number of registered …
[CELEBORN-863] Persist committed file infos to support worker recovery
[CELEBORN-874] Enrich Fetch log
[CELEBORN-876] Enhance log to find out failed workers if data lost
[CELEBORN-878] Convert all IOException to PartitionUnRetryAbleException when openStream/read file
[CELEBORN-882] Add `Pause Push Data Time Count` Metrics & Dashboard Panel
[CELEBORN-883] Optimized configuration checks during MemoryManager initialization
[CELEBORN-886] Support multiple celeborn clusters in the same K8s namespace
[CELEBORN-888] Tweak the logic and add unit tests for the MemoryManager#currentServingState method
[CELEBORN-892] Fix statistics error of commitFiles method
[CELEBORN-897] Set celeborn.network.memory.allocator.allowCache default to false
[CELEBORN-900] Disable jemalloc in non-docker environment
[CELEBORN-900] Prefer to use jemalloc for memory allocation
[CELEBORN-901] Add support for Scala 2.13
[CELEBORN-902] Associate Celeborn.storage.dir with volumes in Helm values.yml
[CELEBORN-904] Bump Spark in spark-3.3 profile from 3.3.2 to 3.3.3
[CELEBORN-905] Redraw the flowchart backpressure.svg after worker pause logic is reconstructed
[CELEBORN-908] Tweak pause and resume logic && add unit test MemoryManager memory check thread
[CELEBORN-920] Worker sends its load to Master through heartbeat
[CELEBORN-922] Improve celeborn shuffle maanger fallback log message
[CELEBORN-924] Change log level to error in PartitionFilesSorter upon Exception
[CELEBORN-933] Add metrics about active shuffle data size
[CELEBORN-934] Make the log description in switchServingState more precise
[CELEBORN-942] Release script supports uploading Nexus
[CELEBORN-943] Pre-create CelebornInputStreams in CelebornShuffleReader
[CELEBORN-945] Change ShutdownHook's timeout for decommission
[CELEBORN-950] Change CelebornShuffleReader log level and information
[CELEBORN-956] Modify parameter passing in AbstractRemoteShuffleInputGateFactory
[CELEBORN-957] Simplify nano time duration calculation
[CELEBORN-958] Log DNS resolution result
[CELEBORN-959] Use Java API to obtain disk capacity information instead of `df` command
[CELEBORN-960] Exclude workers without healthy disks
[CELEBORN-962] Add check DiskInfo#Status in PushDataHandler#checkDiskFull
[CELEBORN-963] Add WORKDIR in celeborn Dockerfile
[CELEBORN-964] Simplify read process output to prevent leak
[CELEBORN-968] Make volume name dynamic in StatefulSet in Helm chart
[CELEBORN-969] Allow user set priorityClass used by celeborn pods
[CELEBORN-971] Should update Charts appVersion when we update project version
[CELEBORN-975] Refactor the check logic to stop the celeborn master and worker
[CELEBORN-976] Introduce script to check master and worker status
[CELEBORN-979] Reduce default disk Check Interval
[CELEBORN-981] Imrpove enable graceful shutdown tips
[CELEBORN-982] Improve RPC bind port tips
[CELEBORN-986] Use formatted log instead of string concat
[CELEBORN-1007] Improve JVM metrics naming and add ThreadStates metrics
[CELEBORN-1008] Adjust push/fetch timeout checker thread pool and tasks
[CELEBORN-1014] Output log with bound address and port
[MINOR] Add an alternative for CLIENT_RESERVE_SLOTS_RACKAWAE_ENABLED
[MINOR] Exclude sbt files from git

### Stability and Bug Fix

[CELEBORN-656] Fix wrong message call when revive return STAGE_END
[CELEBORN-788] Update callback's location should also update the PushState to keep consistent
[CELEBORN-788] Update latest PartitionLocation before retry PushData
[CELEBORN-804] ShuffleClient should cleanup shuffle infos when trigger unregisterShuffle
[CELEBORN-806] Correct the conf key `celeborn.data.io.threads` within the class `ShuffleClientImpl`
[CELEBORN-819] Fix worker graceful shutdown exitKind set
[CELEBORN-844] Fix incorrect config name in ConfigEntity checkvalue method and format message
[CELEBORN-845] Sort memory counter won't decrease after sort failed
[CELEBORN-846] Fix broken link caused by unknown RPC
[CELEBORN-881] StorageManager clean up thread may delete new app directories
[CELEBORN-890] PushHandler should check whether FileWriter has closed to avoid data lost
[CELEBORN-899] Fix potential NPE in ShuffleClientImpl#revive
[CELEBORN-917] Record read metric should be compatible with Gluten shuffle serde
[CELEBORN-926] Enabled GRACEFUL SHUTDOWN, will meet IllegalMonitorStateException
[CELEBORN-928] Don't stop LocalFlusher when notify error
[CELEBORN-932] Fix worker register after gracefaully restart
[CELEBORN-946] Record read metric should be compatible with Gluten shuffle dependency
[CELEBORN-961] Catch exception when constructing Worker
[CELEBORN-972] Enhance workingdirDiskCapacity unit parsing and fix ConfigMap is not effected for workerStatefuleSet
[CELEBORN-988] Add config option to control original unsorted file deletion in `PartitionFilesSorter`
[CELEBORN-988] Rename config key `celeborn.worker.sortPartition.lazyRemovalOfOriginalFiles.enabled`
[CELEBORN-990] Add exception handler when calling CelebornHadoopUtils.getHadoopFS
[CELEBORN-1005] Clean expired app dirs will delete the running application
[CELEBORN-1013] Shutdown master if initialized failed

### Documentation

[CELEBORN-810] Fix some typos and grammar
[CELEBORN-811] Refine monitoring doc
[CELEBORN-814] Merge upgrade doc to Deployment tab and add TOC
[CELEBORN-909] Restore titles in migration guide
[CELEBORN-941] Fix incorrect deploy doc
[CELEBORN-944] Add link about cluster planning
[CELEBORN-984] ShutdownWorkers API documentation
[CELEBORN-1003] Correct the LICENSE and NOTICE for shaded client jars
[CELEBORN-1007] Update Migration Guide
[CELEBORN-1009] CELEBORN_PREFER_JEMALLOC
[MINOR] Fix configuration version
[MINOR] Fix typo in CelebornConf

## Credits
Thanks to the following contributors who helped to review and commit to Apache Celeborn(Incubating) 0.3.1-incubating version:

| Contributors |                |               |               |                |             |
|--------------|----------------|---------------|---------------|----------------|-------------|
| Angerszhuuuu | Aravind Patnam | Cheng Pan     | Fu Chen       | Jun He         | Keyong Zhou |
| Melody       | Shuang         | SteNicholas   | Xiduo You     | camper42       | caojiaqing  |
| hongzhaoyang | jiang13021     | jiaoqingbo    | liangyongyuan | lishiyucn      | mingji      |
| onebox-li    | sunjunjie      | sychen        | xiyu.zk       | zhongqiang.czq | zwangsheng  |
| 宪英          |                |               |               |                |             |



