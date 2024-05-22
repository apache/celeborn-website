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

# Apache Celeborn™ 0.4.1 Release Notes

## Highlight

- Improve flusher's robustness
- Optimize `CelebornInputStreamImpl`'s memory usage
- Fix `Worker#computeResourceConsumption` `NullPointerException` for `userResourceConsumption` that does not contain given userIdentifier
- Fix unregisterShuffle with `celeborn.client.spark.fetch.throwsFetchFailure` disabled

### Improvement

- [CELEBORN-1174] Introduce application dimension resource consumption metrics
- [CELEBORN-1182] Support application dimension `ActiveConnectionCount` metric to record the number of registered connections for each application
- [CELEBORN-1244] Delete redundant remove operations and handle timeout requests in final check
- [CELEBORN-1248] Improve flusher's robustness
- [CELEBORN-1259] Improve the default gracePeriod of `ThreadUtils#shutdown`
- [CELEBORN-1266] Improve log of current failed workers for `WorkerStatusTracker`
- [CELEBORN-1272] Do not increment epoch when retry commit
- [CELEBORN-1278] Avoid calculating all outstanding requests to improve performance
- [CELEBORN-1283] `TransportClientFactory` avoid contention and get or create clientPools quickly
- [CELEBORN-1288] Prompt configuration items when receiving IdleStateEvent
- [CELEBORN-1291] Master crashed causing by huge app level worker consumption info
- [CELEBORN-1292] Remove app level metrics from worker and master
- [CELEBORN-1298] Support Spark 2.4 with Scala 2.12
- [CELEBORN-1300] Optimize `CelebornInputStreamImpl`'s memory usage
- [CELEBORN-1301] Catch and throw `FetchFailedException` in `CelebornInputStream#fillBuffer`
- [CELEBORN-1312] Move `handleRequestPartitions` out of sync block
- [CELEBORN-1315] Manually close the RocksDB/LevelDB instance when `checkVersion` throw Exception
- [CELEBORN-1316] Override `toString` method for `StoreVersion`
- [CELEBORN-1324] Remove unused `PrometheusSink` class
- [CELEBORN-1326] `FakedRemoteInputChannel` use task name of `RemoteShuffleInputGateDelegation` as owningTaskName
- [CELEBORN-1339] Mark connection as timedOut in `TransportClient#close`
- [CELEBORN-1345] Add a limit to the master's estimated partition size
- [CELEBORN-1363] `AbstractRemoteShuffleInputGateFactory` supports `celeborn.client.shuffle.compression.codec` to configure compression codec
- [CELEBORN-1376] Push data failed should always release request body
- [CELEBORN-1379] Catch `Throwable` for `ReadBufferDispatcher` thread
- [CELEBORN-1380] leveldbjni uses org.openlabtesting.leveldbjni to support linux aarch64 platform for leveldb
- [CELEBORN-1381] Avoid construct TransportConf when creating `CelebornInputStream`
- [CELEBORN-1384] Manually excluding workers should not depend on whether the workers are alive
- [CELEBORN-1386] `LevelDBProvider`/`RocksDBProvider` should create non-existent multi-level directory for LevelDB/RocksDB initialization
- [CELEBORN-1391] Retry when MasterClient receiving a `RpcTimeoutException`
- [CELEBORN-1398] Support return leader ip to client
- [CELEBORN-1399] MR `CelebornMapOutputCollector` should check exception after flush
- [CELEBORN-1407] Change log4j2 template appender to file
- [CELEBORN-1408] `workerShuffleCommitTimeout` should use millisecond units
- [CELEBORN-1409] `CommitHandler` commitFiles RPC supports separate timeout configuration
- [CELEBORN-1411] Change default log level to INFO when there is no log4j2 config file
- [CELEBORN-1412] `celeborn.client.rpc.*.askTimeout` should fallback to `celeborn.rpc.askTimeout`

### Stability and Bug Fix

- [CELEBORN-448] `HeartbeatFromApplicationResponse` should include manually excluded workers
- [CELEBORN-863] Fix persisted committed file infos lost
- [CELEBORN-1252] Fix `Worker#computeResourceConsumption` `NullPointerException` for userResourceConsumption that does not contain given userIdentifier
- [CELEBORN-1271] Fix `unregisterShuffle` with `celeborn.client.spark.fetch.throwsFetchFailure` disabled
- [CELEBORN-1275] Fix bug that callback function may hang when unchecked exception missed
- [CELEBORN-1282] Fix `FetchHandler#handleEndStreamFromClient` `NullPointerException` after recycling stream of `CreditStreamManager`
- [CELEBORN-1290] Fix NPE occurring prior to worker registration
- [CELEBORN-1420] Fix mapreduce job will throw an exit exception after it succeeded

### Build

- [CELEBORN-1310] License check add flink-1.19 profile
- [CELEBORN-1404] Disable SBT ANSI color on extracting info from output
- [CELEBORN-1405] SBT allows using credential without a realm

### Documentation

- [CELEBORN-1260] Improve Spark Configuration of Deploy Spark client for deployment document
- [CELEBORN-1295] Add tm to Celeborn's website and change repo_url to apache repo

### Dependencies

- [CELEBORN-1006] Dependency hadoop-client should exclude hadoop-mapreduce-client dependencies for Hadoop 2
- [CELEBORN-1330] Bump rocksdbjni version from 8.5.3 to 8.11.3
- [CELEBORN-1331] Remove third-party dependencies in shaded clients' pom
- [CELEBORN-1366] Bump guava from 32.1.3-jre to 33.1.0-jre

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.4.1 version:

| Contributors |              |              |             |                     |                |
|--------------|--------------|--------------|-------------|---------------------|----------------|
| Angerszhuuuu | Cheng Pan    | Erik.fang    | Ethan Feng  | Fei Wang            | Fu Chen        |
| Fulong Li    | Jiashu Xiong | Kerwin Zhang | Keyong Zhou | Mridul Muralidharan | Nicholas Jiang |
| Qingbo Jiao  | Shaoyun Chen | Yanze Jiang  | Yihe Li     |                     |                |
