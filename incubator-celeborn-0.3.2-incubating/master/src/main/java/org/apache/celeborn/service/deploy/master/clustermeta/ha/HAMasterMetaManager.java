/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.celeborn.service.deploy.master.clustermeta.ha;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.common.CelebornConf;
import org.apache.celeborn.common.client.MasterClient;
import org.apache.celeborn.common.exception.CelebornRuntimeException;
import org.apache.celeborn.common.identity.UserIdentifier;
import org.apache.celeborn.common.meta.AppDiskUsageMetric;
import org.apache.celeborn.common.meta.DiskInfo;
import org.apache.celeborn.common.meta.WorkerInfo;
import org.apache.celeborn.common.quota.ResourceConsumption;
import org.apache.celeborn.common.rpc.RpcEnv;
import org.apache.celeborn.service.deploy.master.clustermeta.AbstractMetaManager;
import org.apache.celeborn.service.deploy.master.clustermeta.MetaUtil;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.ResourceRequest;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.Type;
import org.apache.celeborn.service.deploy.master.network.CelebornRackResolver;

public class HAMasterMetaManager extends AbstractMetaManager {
  private static final Logger LOG = LoggerFactory.getLogger(HAMasterMetaManager.class);

  protected HARaftServer ratisServer;

  public HAMasterMetaManager(RpcEnv rpcEnv, CelebornConf conf) {
    this.rpcEnv = rpcEnv;
    this.conf = conf;
    this.initialEstimatedPartitionSize = conf.initialEstimatedPartitionSize();
    this.estimatedPartitionSize = initialEstimatedPartitionSize;
    this.appDiskUsageMetric = new AppDiskUsageMetric(conf);
    this.rackResolver = new CelebornRackResolver(conf);
  }

  public HARaftServer getRatisServer() {
    return ratisServer;
  }

  public void setRatisServer(HARaftServer ratisServer) {
    this.ratisServer = ratisServer;
  }

  @Override
  public void handleRequestSlots(
      String shuffleKey,
      String hostName,
      Map<String, Map<String, Integer>> workerToAllocatedSlots,
      String requestId) {
    try {
      ResourceProtos.RequestSlotsRequest.Builder builder =
          ResourceProtos.RequestSlotsRequest.newBuilder()
              .setShuffleKey(shuffleKey)
              .setHostName(hostName);
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.RequestSlots)
              .setRequestId(requestId)
              .setRequestSlotsRequest(builder.build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle request slots for {} failed!", shuffleKey, e);
      throw e;
    }
  }

  @Override
  public void handleUnRegisterShuffle(String shuffleKey, String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.UnRegisterShuffle)
              .setRequestId(requestId)
              .setUnregisterShuffleRequest(
                  ResourceProtos.UnregisterShuffleRequest.newBuilder()
                      .setShuffleKey(shuffleKey)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle unregister shuffle for {} failed!", shuffleKey, e);
      throw e;
    }
  }

  @Override
  public void handleAppHeartbeat(
      String appId, long totalWritten, long fileCount, long time, String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.AppHeartbeat)
              .setRequestId(requestId)
              .setAppHeartbeatRequest(
                  ResourceProtos.AppHeartbeatRequest.newBuilder()
                      .setAppId(appId)
                      .setTime(time)
                      .setTotalWritten(totalWritten)
                      .setFileCount(fileCount)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle heartbeat for {} failed!", appId, e);
      throw e;
    }
  }

  @Override
  public void handleAppLost(String appId, String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.AppLost)
              .setRequestId(requestId)
              .setAppLostRequest(ResourceProtos.AppLostRequest.newBuilder().setAppId(appId).build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle app lost for {} failed!", appId, e);
      throw e;
    }
  }

  @Override
  public void handleWorkerLost(
      String host, int rpcPort, int pushPort, int fetchPort, int replicatePort, String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.WorkerLost)
              .setRequestId(requestId)
              .setWorkerLostRequest(
                  ResourceProtos.WorkerLostRequest.newBuilder()
                      .setHost(host)
                      .setRpcPort(rpcPort)
                      .setPushPort(pushPort)
                      .setFetchPort(fetchPort)
                      .setReplicatePort(replicatePort)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle worker lost for {} failed!", host, e);
      throw e;
    }
  }

  @Override
  public void handleWorkerRemove(
      String host, int rpcPort, int pushPort, int fetchPort, int replicatePort, String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.WorkerRemove)
              .setRequestId(requestId)
              .setWorkerRemoveRequest(
                  ResourceProtos.WorkerRemoveRequest.newBuilder()
                      .setHost(host)
                      .setRpcPort(rpcPort)
                      .setPushPort(pushPort)
                      .setFetchPort(fetchPort)
                      .setReplicatePort(replicatePort)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle worker lost for {} failed!", host, e);
      throw e;
    }
  }

  @Override
  public void handleRemoveWorkersUnavailableInfo(
      List<WorkerInfo> unavailableWorkers, String requestId) {
    try {
      List<ResourceProtos.WorkerAddress> addrs =
          unavailableWorkers.stream().map(MetaUtil::infoToAddr).collect(Collectors.toList());
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.RemoveWorkersUnavailableInfo)
              .setRequestId(requestId)
              .setRemoveWorkersUnavailableInfoRequest(
                  ResourceProtos.RemoveWorkersUnavailableInfoRequest.newBuilder()
                      .addAllUnavailable(addrs)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle remove workers unavailable info failed!", e);
      throw e;
    }
  }

  @Override
  public void handleWorkerHeartbeat(
      String host,
      int rpcPort,
      int pushPort,
      int fetchPort,
      int replicatePort,
      Map<String, DiskInfo> disks,
      Map<UserIdentifier, ResourceConsumption> userResourceConsumption,
      Map<String, Long> estimatedAppDiskUsage,
      long time,
      boolean highWorkload,
      String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.WorkerHeartbeat)
              .setRequestId(requestId)
              .setWorkerHeartbeatRequest(
                  ResourceProtos.WorkerHeartbeatRequest.newBuilder()
                      .setHost(host)
                      .setRpcPort(rpcPort)
                      .setPushPort(pushPort)
                      .setFetchPort(fetchPort)
                      .setReplicatePort(replicatePort)
                      .putAllDisks(MetaUtil.toPbDiskInfos(disks))
                      .putAllUserResourceConsumption(
                          MetaUtil.toPbUserResourceConsumption(userResourceConsumption))
                      .putAllEstimatedAppDiskUsage(estimatedAppDiskUsage)
                      .setTime(time)
                      .setHighWorkload(highWorkload)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle worker heartbeat for {} failed!", host, e);
      throw e;
    }
  }

  @Override
  public void handleRegisterWorker(
      String host,
      int rpcPort,
      int pushPort,
      int fetchPort,
      int replicatePort,
      Map<String, DiskInfo> disks,
      Map<UserIdentifier, ResourceConsumption> userResourceConsumption,
      String requestId) {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.RegisterWorker)
              .setRequestId(requestId)
              .setRegisterWorkerRequest(
                  ResourceProtos.RegisterWorkerRequest.newBuilder()
                      .setHost(host)
                      .setRpcPort(rpcPort)
                      .setPushPort(pushPort)
                      .setFetchPort(fetchPort)
                      .setReplicatePort(replicatePort)
                      .putAllDisks(MetaUtil.toPbDiskInfos(disks))
                      .putAllUserResourceConsumption(
                          MetaUtil.toPbUserResourceConsumption(userResourceConsumption))
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle worker register for {} failed!", host, e);
      throw e;
    }
  }

  @Override
  public void handleReportWorkerUnavailable(List<WorkerInfo> failedNodes, String requestId) {
    try {
      List<ResourceProtos.WorkerAddress> addrs =
          failedNodes.stream().map(MetaUtil::infoToAddr).collect(Collectors.toList());
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.ReportWorkerUnavailable)
              .setRequestId(requestId)
              .setReportWorkerUnavailableRequest(
                  ResourceProtos.ReportWorkerUnavailableRequest.newBuilder()
                      .addAllUnavailable(addrs)
                      .build())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle report node failure for {} failed!", failedNodes, e);
      throw e;
    }
  }

  @Override
  public void handleUpdatePartitionSize() {
    try {
      ratisServer.submitRequest(
          ResourceRequest.newBuilder()
              .setCmdType(Type.UpdatePartitionSize)
              .setRequestId(MasterClient.genRequestId())
              .build());
    } catch (CelebornRuntimeException e) {
      LOG.error("Handle update partition size failed!", e);
      throw e;
    }
  }
}
