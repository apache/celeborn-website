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

Storage Usage Quota Guide
===
This documentation describes how RSS limits user's storage resource usage by setting quota.

## Quota limitation

RSS has a configurable user storage quota system. This allows RSS admin to manage each user's
max resource usage to keep RSS cluster more stable. This feature can avoid RSS cluster resources
being occupied by the minority with huge applications.

RSS cluster's worker will collect each user's resource consumption and report the information
to master in register and heartbeat message. When `LifecycleManager` register shuffle to RSS master,
will check current user's resource usage, if used resource exceed the quota setting,
shuffle will be fallback to ESS.

## Storage resource

Currently, RSS support two storage levels:
  1. Local disk.
  2. HDFS.

And there are two levels of resources: 
  1. written bytes.
  2. written file numbers.

So, now we support four setting about quota:

  1. diskBytesWritten
  2. diskFileCount
  3. hdfsBytesWritten
  4. hdfsFileCount

If not set, default quota value is `-1`, means there is no limit for this user.

## Configuration

The quota system is configured via a configuration yaml file that RSS expects to be present at
`$RSS_HOME/conf/quota.yaml`.  A custom file location can be specified via the
`rss.quota.configuration.path` configuration property. The quota yaml configuration
file should be organized as a list, each part setting one user's quota. In the `quota` section, set each quota's threshold value.
Notice: quota value should be numeric value that can be cast to `Long` type.
For example:

```text
-  tenantId: AAA
   name: Tom
   quota:
     diskBytesWritten: 10000
     diskFileCount: 200
     hdfsBytesWritten: -1
     hdfsFileCount: -1

-  tenantId: BBB
   name: Jerry
   quota:
     diskBytesWritten: -1
     diskFileCount: -1
     hdfsBytesWritten: 10000
     hdfsFileCount: 200
```
