---
license: |
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements. See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License. You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
---


Monitoring
===

There are two ways to monitor Celeborn cluster: prometheus metrics and REST API.

# Metrics

# REST API

In addition to viewing the metrics, Celeborn also support REST API. This gives developers
an easy way to create new visualizations and monitoring tools for Celeborn and
also easy for users to get the running status of the service. The REST API is available for
both master and worker. The endpoints are mounted at `host:port`. For example,
for the master, they would typically be accessible
at `http://<master-prometheus-host>:<master-prometheus-port>/<path>`, and
for the worker, at `http://<worker-prometheus-host>:<worker-prometheus-port>/<path>`.

The configuration of `<master-prometheus-host>`, `<master-prometheus-port>`, `<worker-prometheus-host>`, `<worker-prometheus-port>` as below:

| Key                                     | Default | Description                | Since |
|-----------------------------------------|---------|----------------------------| ----- |
| celeborn.master.metrics.prometheus.host | 0.0.0.0 | Master's Prometheus host.  | 0.2.0 |
| celeborn.master.metrics.prometheus.port | 9098    | Master's Prometheus port.  | 0.2.0 |
| celeborn.worker.metrics.prometheus.host | 0.0.0.0 | Worker's Prometheus host.  | 0.2.0 |
| celeborn.worker.metrics.prometheus.port | 9096    | Worker's Prometheus port.  | 0.2.0 |

API path listed as below:

| Path                       | Service        | Meaning                                                                                                                                                                              |
|----------------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| /conf                      | master, worker | List the conf setting of the service.                                                                                                                                                |
| /workerInfo                | master, worker | List worker information of the service. For the master, it will list all registered workers 's information.                                                                          |
| /lostWorkers               | master         | List all lost workers of the master.                                                                                                                                                 |
| /blacklistedWorkers        | master         | List all  blacklisted workers of the master.                                                                                                                                         |
| /threadDump                | master, worker | List the current thread dump of the service.                                                                                                                                         |
| /hostnames                 | master         | List all running application's LifecycleManager's hostnames of the cluster.                                                                                                          |
| /applications              | master         | List all running application's ids of the cluster.                                                                                                                                   |
| /shuffles                  | master, worker | List all running shuffle keys of the service. For master, will return all running shuffle's key of the cluster, for worker, only return keys of shuffles running in that worker.     |
| /listTopDiskUsedApps       | master, worker | List the top disk usage application ids. For master, will return the top disk usage application ids for the cluster, for worker, only return application ids running in that worker. |
| /listPartitionLocationInfo | worker         | List all living PartitionLocation information in that worker.                                                                                                                        |
| /unavailablePeers          | worker         | List the unavailable peers of the worker, this always means the worker connect to the peer failed.                                                                                   |
| /isShutdown                | worker         | Show if the worker is during the process of shutdown.                                                                                                                                |
| /isRegistered              | worker         | Show if the worker is registered to the master success.                                                                                                                              |