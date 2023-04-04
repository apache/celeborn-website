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

# Deploy Celeborn On Kubernetes

Celeborn currently supports rapid deployment by using helm.

## Before Deploy

1. You should have a Running Kubernetes Cluster.
2. You should understand simple Kubernetes deploy related,
   e.g. [Kubernetes Resources](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/).
3. You have
   enough [permissions to create resources](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig/).
4. Installed [Helm](https://helm.sh/docs/intro/install/).

## Deploy

### 1. Get Celeborn Binary Package

You can find released version of Celeborn on https://celeborn.apache.org/download/.

Of course, you can build binary package from master branch or your own branch by using `./build/make-distribution.sh` in
source code.

Anyway, you should unzip and into binary package.

### 2. Modify Celeborn Configurations

> Notice: Celeborn Charts Template Files is in the experimental instability stage, the subsequent optimization will be
> adjusted.

The configuration in `./charts/celeborn/values.yaml` you should focus on modifying is:

* image repository - Get images from which repository
* image tag - Which version of image to use
* masterReplicas - Number of celeborn master replicas
* workerReplicas - Number of celeborn worker replicas
* celeborn `celeborn.worker.storage.dirs` - which disk should be mounted for celeborn worker(For more
  information, [HostPath](https://kubernetes.io/docs/concepts/storage/volumes/#hostpath))

### [Optional] Build Celeborn Docker Image

Maybe you want to make your own celeborn docker image, you can use `docker build . -f docker/Dockerfile` in Celeborn
Binary.

### 3. Helm Install Celeborn Charts

More details in [Helm Install](https://helm.sh/docs/helm/helm_install/)

```
cd ./charts/celeborn

helm install celeborn -n <namespace> .
```

### 4. Check Celeborn

After the above operation, you should be able to find the corresponding Celeborn Master/Worker
by `kubectl get pods -n <namespace>`

Etc.

```
NAME                READY     STATUS             RESTARTS   AGE
celeborn-master-0   1/1       Running            0          1m
...
celeborn-worker-0   1/1       Running            0          1m
...
```

Given that Celeborn Master/Worker takes time to start, you can see the following phenomenon:

```
** server can't find celeborn-master-0.celeborn-master-svc.default.svc.cluster.local: NXDOMAIN

waiting for master
Server:         172.17.0.10
Address:        172.17.0.10#53

...

Name:   celeborn-master-0.celeborn-master-svc.default.svc.cluster.local
Address: 10.225.139.80

Server:         172.17.0.10
Address:        172.17.0.10#53

starting org.apache.celeborn.service.deploy.master.Master, logging to /opt/celeborn/logs/celeborn--org.apache.celeborn.service.deploy.master.Master-1-celeborn-master-0.out

...

23/03/23 14:10:56,081 INFO [main] RaftServer: 0: start RPC server
23/03/23 14:10:56,132 INFO [nioEventLoopGroup-2-1] LoggingHandler: [id: 0x83032bf1] REGISTERED
23/03/23 14:10:56,132 INFO [nioEventLoopGroup-2-1] LoggingHandler: [id: 0x83032bf1] BIND: 0.0.0.0/0.0.0.0:9872
23/03/23 14:10:56,134 INFO [nioEventLoopGroup-2-1] LoggingHandler: [id: 0x83032bf1, L:/0:0:0:0:0:0:0:0:9872] ACTIVE
23/03/23 14:10:56,135 INFO [JvmPauseMonitor0] JvmPauseMonitor: JvmPauseMonitor-0: Started
23/03/23 14:10:56,208 INFO [main] Master: Metrics system enabled.
23/03/23 14:10:56,216 INFO [main] HttpServer: master: HttpServer started on port 9098.
23/03/23 14:10:56,216 INFO [main] Master: Master started.
```

### 5. Build Celeborn Client

Here, without going into detail on how to configure spark/flink to find celeborn master/worker, mention the key
configuration:

```
spark.celeborn.master.endpoints: celeborn-master-0.celeborn-master-svc.<namespace>:9097,celeborn-master-1.celeborn-master-svc.<namespace>:9097,celeborn-master-2.celeborn-master-svc.<namespace>:9097
```

You can find why config endpoints such way
in [Kubernetes DNS for Service And Pods](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)

> Notice: You should ensure that Spark/Flink can find the Celeborn Master/Worker via IP or the Kubernetes DNS mentioned
> above
