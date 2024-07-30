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

# Apache Celeborn™ 0.5.1 Release Notes

## Highlight
- Fix an issue that may cause data loss  
- Improve shuffle read performance

### Improvements
- [CELEBORN-1516] DynamicConfigServiceFactory should support singleton
- [CELEBORN-1515] SparkShuffleManager should set lifecycleManager to null after stopping lifecycleManager in Spark 2
- [CELEBORN-1509] Reply response without holding a lock
- [CELEBORN-1495] CelebornColumnDictionary supports dictionary of float and double column type
- [CELEBORN-1491][FOLLOWUP] Add flusher working queue size metric into grafana dashboard
- [CELEBORN-1489] Update Flink support with authentication support
- [CELEBORN-1485] Refactor addCounter, addGauge and addTimer of AbstractSource to reduce CPU utilization
- [CELEBORN-1479] Report register shuffle failed reason in exception
- [CELEBORN-1476] Enhance the RESTful response error msg
- [CELEBORN-1472] Reduce CongestionController#userBufferStatuses call times
- [CELEBORN-1459][FOLLOWUP] Introduce CleanTaskQueueSize and CleanExpiredShuffleKeysTime to record situation of cleaning up expired shuffle keys
- [CELEBORN-1446] Enable chunk prefetch when initialize CelebornInputStream
- [CELEBORN-914][FOLLOWUP] optimize write and sort logic for memory storage

### Stability and Bug Fix
- [CELEBORN-1507] Prevent invalid Filegroups from being used
- [CELEBORN-1506][BUG] Revert "[CELEBORN-1036][FOLLOWUP] totalInflightReqs should decrement when batchIdSet contains the batchId to avoid duplicate caller of removeBatch"
- [CELEBORN-1494] Support IPv6 addresses in PbSerDeUtils.fromPackedPartitionLocations
- [CELEBORN-1478] Fix wrong use partitionId as shuffleId when readPartition
- [CELEBORN-1475] Fix unknownExcludedWorkers filter for /exclude request
- [CELEBORN-1471] CelebornScalaObjectMapper supports configuring FAIL_ON_UNKNOWN_PROPERTIES to false
- [CELEBORN-1439] Fix revive logic bug which will casue data correctness issue and job failiure
- [CELEBORN-1317][FOLLOWUP] Fix media type annotations for form urlencoded APIs
- [CELEBORN-914][FOLLOWUP] Restore select flush worker index logic

### Documentation
- [CELEBORN-1486] Introduce ClickHouse Backend in Gluten Support document
- [CELEBORN-1466] Add local command in celeborn_ratis_shell.md

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.5.1 version:

| Contributors |             |                |                     |         |          |
|--------------|-------------|----------------|---------------------|---------|----------|
| FMX          | jiang13021  | leixm          | Mridul Muraildharan | pan3793 | RexXiong |
| SteNicholas  | turboFei    | waitinfuture   |       zhaostu4      |         |          |


