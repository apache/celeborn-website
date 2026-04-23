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

# Apache Celeborn™ 0.6.3 Release Notes

## Highlight

- Parallelize the create partition writer in handleReserveSlots to speed up the reserveSlots RPC process time
- Fast fail reduce stage if shuffle data is lost because of worker lost
- Fix replicate channels not resumed when transitioning from PUSH_AND_REPLICATE_PAUSED to PUSH_PAUSED
- Fix RuntimeException during stream cleanup preventing peer failover
- Fix ArithmeticException when PUSH_DATA_HAND_SHAKE fails before any data written

### Correctness

- [CELEBORN-2238] Fix RuntimeException during stream cleanup preventing peer failover
- [CELEBORN-2274] Fix replicate channels not resumed when transitioning from PUSH_AND_REPLICATE_PAUSED to PUSH_PAUSED

### Improvement

- [CELEBORN-2063] Parallelize the create partition writer in handleReserveSlots to speed up the reserveSlots RPC process time
- [CELEBORN-2166] Fast fail reduce stage if shuffle data is lost because of worker lost
- [CELEBORN-2212] Optimize the sorting efficiency of memoryWriters when evicting the largest memory file
- [CELEBORN-2214] Add extraInitContainers for worker in helm
- [CELEBORN-2243] During the close phase of hashWriter, pushData and mergeData are sent in parallel
- [CELEBORN-2248] Implement lazy loading for columnar shuffle classes and skew shuffle method using static holder pattern
- [CELEBORN-2256] Helm chart: add support for setting annotations on the service account (to support eks.amazonaws.com/role-arn)
- [CELEBORN-2265] Do not waste resources on hotpath for debug logging in HashBasedShuffleWriter and SortBasedShuffleWriter
- [CELEBORN-2271] StorageManager#saveCommittedFileInfosExecutor should call shutdown before awaitTermination
- [CELEBORN-2272] Add LZ4TPCDSDataBenchmark
- [CELEBORN-2277] Replace synchronized in Flusher.getWorkerIndex with AtomicInteger
- [CELEBORN-2281] Improve error logging and null checks in CreditStreamManager
- [CELEBORN-2287] Split mode should be HARD_SPLIT when disk is full
- [CELEBORN-2294] The shuffle fetch failed report from the zombie stage should be ignored
- [CELEBORN-2295] CommitHandler should support retry interval

### Stability and Bug Fix

- [CELEBORN-1577] Fix master resource consumption metrics
- [CELEBORN-2063] Fix timeout unit for parallel creation of partition writer
- [CELEBORN-2238] Fix RuntimeException during stream cleanup preventing peer failover
- [CELEBORN-2263] Fix IndexOutOfBoundsException while reading from S3
- [CELEBORN-2273] Fix cache mutation in TagsManager.getTaggedWorkers()
- [CELEBORN-2274] Fix replicate channels not resumed when transitioning from PUSH_AND_REPLICATE_PAUSED to PUSH_PAUSED
- [CELEBORN-2276] Fix race condition in MemoryManager.releaseSortMemory
- [CELEBORN-2283] Fix missing return in Master.handleRequestSlots when all workers are excluded
- [CELEBORN-2284] Fix TLS Memory Leak
- [CELEBORN-2292] Fix ArithmeticException when PUSH_DATA_HAND_SHAKE fails before any data written
- [CELEBORN-2293] Fix ConcurrentModificationException in WorkerStatusTracker.shuttingWorkers
- [CELEBORN-2296] Fix race condition in MemoryManager singleton initialization
- [CELEBORN-2302] Fix NPE in MemoryManager.close() when readBufferDispatcher is not initialized
- [CELEBORN-2304] Fix timeout unit mismatch in disk monitor check

### Dependencies

- [CELEBORN-2218] Bump lz4-java version from 1.8.0 to 1.10.4 to resolve CVE‐2025‐12183 and CVE-2025-66566
- [CELEBORN-2231] Upgrade jersey version to 2.47 to fix CVE-2025-12383
- [CELEBORN-2234] Bump jetty version to 9.4.58.v20250814 to fix GHSA-qh8g-58pp-2wxh

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.6.3 version:

| Contributors   |              |                  |                 |              |                    |
|----------------|--------------|------------------|-----------------|--------------|--------------------|
| Aravind Patnam | Cheng Pan    | Dongdong Zhang   | Enrico Olivelli | Fei Wang     | Gen Luo            |
| Hai Zhou       | Huan Zheng   | Kartikay Bhutani | Nicholas Jiang  | Ping Zhang   | Prateek Srivastava |
| Sanskar Modi   | Shaoyun CHen | Shlomi Tubul     | Shuai Lu        | Xianming Lei | Zhaohui Xu         |
| Zhengtao Shuai |              |                  |                 |              |                    |
