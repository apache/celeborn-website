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

# Apache Celeborn™ 0.5.2 Release Notes

## Highlight

- Optimize LifecycleManager Rpc performance
- Worker can release disk buffer when OOM happened
- Fix packed partition location cause GetReducerFileGroupResponse lose location

### Improvement

- [CELEBORN-1240][FOLLOWUP] Introduce web profile for web module
- [CELEBORN-1500] Filter out empty InputStreams
- [CELEBORN-1725] Optimize performance of handling MapperEnd RPC in LifecycleManager
- [CELEBORN-1725][FOLLOWUP] Optimize isAllMapTasksEnd performance
- [CELEBORN-1782] Worker in congestion control should be in blacklist to avoid impact new shuffle

### Stability and Bug Fix

- Revert "[CELEBORN-1376] Push data failed should always release request body"
- [CELEBORN-1510] Partial task unable to switch to the replica
- [CELEBORN-1701][FOLLOWUP] Support stage rerun for shuffle data lost
- [CELEBORN-1759] Fix reserve slots might lost partition location between 0.4 client and 0.5 server
- [CELEBORN-1760] OOM causes disk buffer unable to be released
- [CELEBORN-1763] Fix DataPusher be blocked for a long time
- [CELEBORN-1765] Fix NPE when removeFileInfo in StorageManager
- [CELEBORN-1769] Fix packed partition location cause GetReducerFileGroupResponse lose location
- [CELEBORN-1770] FlushNotifier should setException for all Throwables in Flusher
- [CELEBORN-1743] Resolve the metrics data interruption and the job failure caused by locked resources
- [CELEBORN-1783] Fix Pending task in commitThreadPool wont be canceled
- [CELEBORN-1783][FOLLOWUP] Compatible with UT

### Build

- [CELEBORN-1816] Bump scala-maven-plugin to avoid compilation loop

### Documentation

- [CELEBORN-1752] Migration guide for unexpected shuffle RESTful api change since 0.5.0

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.5.2 version:

| Contributors |              |           |           |          |             |
|--------------|--------------|-----------|-----------|----------|-------------|
| cfmcgrady    | FMX          | leixm     | onebox-li | RexXiong | SteNicholas |
| turboFei     | waitinfuture | zaynt4606 | zhaostu4  |          |             |
