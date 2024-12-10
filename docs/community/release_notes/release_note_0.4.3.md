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

# Apache Celeborn™ 0.4.3 Release Notes

- [CELEBORN-1686][FOLLOWUP] Avoid NPE when clean up data pusher
- [CELEBORN-1725][FOLLOWUP] Optimize `isAllMapTasksEnd` performance
- [CELEBORN-1725] Optimize performance of handling `MapperEnd` RPC in `LifecycleManager`
- [CELEBORN-1485][0.4] Refactor addCounter, addGauge and addTimer of AbstractSource to reduce CPU utilization
- [CELEBORN-1556][0.4] Update Github actions to v4
- [CELEBORN-1686] Avoid return the same pushTaskQueue
- [CELEBORN-1677][BUILD] Update SCM information for SBT build configuration
- [CELEBORN-1666] Bump scala-protoc from 1.0.6 to 1.0.7
- [CELEBORN-1652] Throw TransportableError for failure of sending PbReadAddCredit to avoid flink task get stuck
- [CELEBORN-1643] DataPusher handle InterruptedException
- [CELEBORN-1058][FOLLOWUP] Update name of master service from MasterSys to Master in startup document
- [CELEBORN-1506][FOLLOWUP] InFlightRequestTracker should not reset totalInflightReqs for cleaning up to avoid negative totalInflightReqs for limitZeroInFlight
- [CELEBORN-1544][0.4] ShuffleWriter needs to call close finally to avoid memory leaks
- [CELEBORN-1496][0.4] Differentiate map results with only different stageAttemptId
- [CELEBORN-1578] Make Worker#timer have thread name and daemon
- [CELEBORN-1575] TimeSlidingHub should remove expire node when reading
- [CELEBORN-1573] Change to debug logging on client side for reserve slots
- [CELEBORN-1570] Fix flaky test - monitor non-critical error metrics in DeviceMonitorSuite
- [CELEBORN-1567] Support throw FetchFailedException when Data corruption detected
- [CELEBORN-1558] Fix the incorrect decrement of pendingWrites in handlePushMergeData
- [CELEBORN-1533] Log location when CelebornInputStream#fillBuffer fails
- [CELEBORN-1520] Minor logging fix for AppDiskUsageMetric and Fixed UTs
- [CELEBORN-1522] Fix applicationId extraction from shuffle key

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.4.3 version:

| Contributors    |              |              |                |              |               |
|-----------------|--------------|--------------|----------------|--------------|---------------|
| Aravind Patnam  | Bowen Liang  | Cheng Pan    | Fu Chen        | Sanskar Modi | Shuang        |
| SteNicholas     | Fei Wang     | Xianming Lei | codenohup      | jiang13021   | Shaoyun Chen  |
| wuziyi          |              |              |                |              |               |
