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

# Apache Celeborn™ 0.5.4 Release Notes

## Highlight

- Support retry when sending RPC to LifecycleManager
- Support custom implementation of EventExecutorChooser to avoid deadlock when calling await in EventLoop thread
- Interrupt spark task should not report fetch failure
- Fix flink client memory leak of TransportResponseHandler#outstandingRpcs for handling addCredit response

### Improvement

- [CELEBORN-1757] Add retry when sending RPC to LifecycleManager
- [CELEBORN-1841] Support custom implementation of EventExecutorChooser to avoid deadlock when calling await in EventLoop thread
- [CELEBORN-1859] DfsPartitionReader and LocalPartitionReader should reuse pbStreamHandlers get from BatchOpenStream request
- [CELEBORN-1882] Support configuring the SSL handshake timeout for SSLHandler
- [CELEBORN-1897] Avoid calling toString for too long messages

### Stability and Bug Fix

- [CELEBORN-1818] Fix incorrect timeout exception when waiting on no pending writes
- [CELEBORN-1838] Interrupt spark task should not report fetch failure
- [CELEBORN-1846] Fix the StreamHandler usage in fetching chunk when task attempt is odd
- [CELEBORN-1850] Setup worker endpoint after initalizing controller
- [CELEBORN-1865] Update master endpointRef when master leader is abnormal
- [CELEBORN-1867] Fix flink client memory leak of TransportResponseHandler#outstandingRpcs for handling addCredit response
- [CELEBORN-1883] Replace HashSet with ConcurrentHashMap.newKeySet for ShuffleFileGroups
- [CELEBORN-1885] Fix nullptr exceptions in FetchChunk after worker restart

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn 0.5.4 version:

| Contributors |            |             |         |              |            |
|--------------|------------|-------------|---------|--------------|------------|
| Aidar Bariev | Ethan Feng | Minchu Yang | Nan Zhu | Sanskar Modi | Xinyu Wang |
| Xu Hang      | Yihe Li    | Zaynt Shuai | Ziyi Wu |              |            |
