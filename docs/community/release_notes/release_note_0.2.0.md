---
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

# Apache Celeborn(Incubating) 0.2.0 Release Notes

## Highlight

- Support load-aware slots allocation strategy.

- Support columnar shuffle.

- Enhanced flow control.

- Enhanced HA stability.

- Enhanced disk management.

- Support rolling upgrade.

- Support quota management.

- Support zstd compression.

- Support spark 2.4 to 3.3.

- Major improvements of Celeborn on Kubernetes, including documents and Helm Chart.

- Code cleanup and refactor.

- Shade netty completely.

- Enhanced build system.

- Refactor configurations.

- Support JDK11.

- Remove log4j1.

### Performance

- Batch and parallelize RPC.

- Add bitmap to filter unnecessary partition read.

- Cache RPC to reduce Spark driver memory pressure.

### Others

- Bugfixes and improvements.

## Credits

Thanks to the following contributors who helped to review and commit to Apache Celeborn(Incubating)
0.2.0-incubating version, and the order is based on the commit time:

| Developers |                |               |              |            |           |
|------------|----------------|---------------|--------------|------------|-----------|
| zwangsheng | AngersZhuuuu   | FMX           | waitinfuture | dxheming   | haiming   |
| pan3793    | lichaojacobs   | liugs0213     | nafiyAix     | fanyilun   | 942011334 |
| kerwin-zk  | xunxunmimi5577 | mcdull-zhang  | wForget      | RexXiong   | leesf     |
| zouxxyy    | Gabriel39      | zhongqiangczq | szyWilliam   | boneanxs   | jxysoft   |
| cfmcgrady  | zy-jordan      | kaijchen      | Rex(Hui) An  | jiaoqingbo |           |
