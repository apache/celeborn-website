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

# Apache Celeborn™ 0.4.2 Release Notes

[CELEBORN-1515] SparkShuffleManager should set lifecycleManager to null after stopping lifecycleManager in Spark 2
[CELEBORN-1507] Prevent invalid Filegroups from being used
[CELEBORN-1509] Reply response without holding a lock
[CELEBORN-1506][BUG] Revert "[CELEBORN-1036][FOLLOWUP] totalInflightReqs should decrement when batchIdSet contains the batchId to avoid duplicate caller of removeBatch"
[CELEBORN-1486] Introduce ClickHouse Backend in Gluten Support document
[CELEBORN-1478] Fix wrong use partitionId as shuffleId when readPartition
[CELEBORN-1475] Fix unknownExcludedWorkers filter for /exclude request
[CELEBORN-1467] celeborn.worker.storage.dirs should support soft link
[CELEBORN-1460][FOLLOWUP] MRAppMasterWithCeleborn support uri of absolute conf path for mapreduce.job.cache.files
[CELEBORN-1463][FOLLOWUP] Respeact client/server threads num to avoid competitiveness
[CELEBORN-1463] Create network memory allocator with celeborn.network.memory.allocator.numArenas
[CELEBORN-1457][0.4] Avoid NPE during shuffle data cleanup
[CELEBORN-1453] Fix the thread safety bug in getMetrics (#2566)
[CELEBORN-1432] ShuffleClientImpl should invoke loadFileGroupInternal only once when using the reduce partition mode (#2567)
[MINOR] Bump minikube and kubernetes version of integration test
[CELEBORN-1452][0.4] Master follower node metadata is out of sync after installing snapshot
[CELEBORN-1182][FOLLOWUP] Fix WorkerSource record application active connection for application dimension ActiveConnectionCount metric
[CELEBORN-1448] Use static regex Pattern instances in JavaUtils.timeStringAs and JavaUtils.byteStringAs
[CELEBORN-1380][FOLLOWUP] leveldbjni uses org.openlabtesting.leveldbjni to support linux aarch64 platform for leveldb via aarch64 profile
[MINOR] Remove incubator in vcs.xml
[CELEBORN-1410] Combine multiple ShuffleBlockInfo into a single ShuffleBlockInfo
[CELEBORN-1380][FOLLOWUP] Add org.openlabtesting.leveldbjni in BSD 3-clause of LICENSE-binary

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.4.1 version:

| Contributors    |              |              |                |              |               |
|-----------------|--------------|--------------|----------------|--------------|---------------|
| Fei Wang        | Jiashu Xiong | Keyong Zhou  | Nicholas Jiang | Sanskar Modi | Shaoyun Chen  |
| Xianming Lei    | Xinyu Wang   | Yanze Jiang  | ZHAO ZHAO      |              |               |
