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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ratis.statemachine.SnapshotInfo;
import org.junit.Assert;
import org.junit.Test;

import org.apache.celeborn.common.CelebornConf;
import org.apache.celeborn.common.identity.UserIdentifier;
import org.apache.celeborn.common.meta.AppDiskUsageSnapShot;
import org.apache.celeborn.common.meta.DiskInfo;
import org.apache.celeborn.common.meta.WorkerInfo;
import org.apache.celeborn.common.quota.ResourceConsumption;
import org.apache.celeborn.common.util.JavaUtils;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.RequestSlotsRequest;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.ResourceRequest;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.ResourceResponse;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.Type;

public class MasterStateMachineSuiteJ extends RatisBaseSuiteJ {

  @Test
  public void testRunCommand() {
    StateMachine stateMachine = ratisServer.getMasterStateMachine();

    Map<String, Integer> allocations = new HashMap<>();
    allocations.put("disk1", 15);
    allocations.put("disk2", 20);

    Map<String, ResourceProtos.SlotInfo> workerAllocations = new HashMap<>();
    workerAllocations.put(
        new WorkerInfo("host1", 1, 2, 3, 10).toUniqueId(),
        ResourceProtos.SlotInfo.newBuilder().putAllSlot(allocations).build());
    workerAllocations.put(
        new WorkerInfo("host2", 2, 3, 4, 11).toUniqueId(),
        ResourceProtos.SlotInfo.newBuilder().putAllSlot(allocations).build());
    workerAllocations.put(
        new WorkerInfo("host3", 3, 4, 5, 12).toUniqueId(),
        ResourceProtos.SlotInfo.newBuilder().putAllSlot(allocations).build());

    RequestSlotsRequest requestSlots =
        RequestSlotsRequest.newBuilder()
            .setShuffleKey("appId-1-1")
            .setHostName("hostname")
            .putAllWorkerAllocations(workerAllocations)
            .build();

    ResourceRequest request =
        ResourceRequest.newBuilder()
            .setRequestSlotsRequest(requestSlots)
            .setCmdType(Type.RequestSlots)
            .setRequestId(UUID.randomUUID().toString())
            .build();

    ResourceResponse response = stateMachine.runCommand(request, -1);
    Assert.assertEquals(response.getSuccess(), true);
  }

  @Test
  public void testTakeSnapshot() {
    final StateMachine stateMachine = ratisServer.getMasterStateMachine();

    stateMachine.notifyTermIndexUpdated(2020, 725);

    final long snapshot1Index = stateMachine.takeSnapshot();
    Assert.assertEquals(725, snapshot1Index);

    SnapshotInfo snapshot1 = stateMachine.getLatestSnapshot();
    Assert.assertEquals(2020, snapshot1.getTerm());
    Assert.assertEquals(725, snapshot1.getIndex());
    Assert.assertEquals(1, snapshot1.getFiles().size());

    stateMachine.notifyTermIndexUpdated(2020, 1005);
    final long snapshot2Index = stateMachine.takeSnapshot();
    Assert.assertEquals(1005, snapshot2Index);

    SnapshotInfo latest = stateMachine.getLatestSnapshot();
    Assert.assertEquals(2020, latest.getTerm());
    Assert.assertEquals(1005, latest.getIndex());
    Assert.assertEquals(1, latest.getFiles().size());
  }

  @Test
  public void testObjSerde() throws IOException, InterruptedException {
    CelebornConf conf = new CelebornConf();
    HAMasterMetaManager masterStatusSystem = new HAMasterMetaManager(null, conf);
    File tmpFile = File.createTempFile("tef", "test" + System.currentTimeMillis());

    Map<String, DiskInfo> disks1 = new HashMap<>();
    disks1.put("disk1", new DiskInfo("disk1", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks1.put("disk2", new DiskInfo("disk2", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks1.put("disk3", new DiskInfo("disk3", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    Map<UserIdentifier, ResourceConsumption> userResourceConsumption1 =
        JavaUtils.newConcurrentHashMap();
    userResourceConsumption1.put(
        new UserIdentifier("tenant1", "name1"), new ResourceConsumption(1000, 1, 1000, 1));
    userResourceConsumption1.put(
        new UserIdentifier("tenant1", "name2"), new ResourceConsumption(2000, 2, 2000, 2));
    userResourceConsumption1.put(
        new UserIdentifier("tenant1", "name3"), new ResourceConsumption(3000, 3, 3000, 3));

    Map<String, DiskInfo> disks2 = new HashMap<>();
    disks2.put("disk1", new DiskInfo("disk1", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks2.put("disk2", new DiskInfo("disk2", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks2.put("disk3", new DiskInfo("disk3", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    Map<UserIdentifier, ResourceConsumption> userResourceConsumption2 =
        JavaUtils.newConcurrentHashMap();
    userResourceConsumption2.put(
        new UserIdentifier("tenant2", "name1"), new ResourceConsumption(1000, 1, 1000, 1));
    userResourceConsumption2.put(
        new UserIdentifier("tenant2", "name2"), new ResourceConsumption(2000, 2, 2000, 2));
    userResourceConsumption2.put(
        new UserIdentifier("tenant2", "name3"), new ResourceConsumption(3000, 3, 3000, 3));

    Map<String, DiskInfo> disks3 = new HashMap<>();
    disks3.put("disk1", new DiskInfo("disk1", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks3.put("disk2", new DiskInfo("disk2", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    disks3.put("disk3", new DiskInfo("disk3", 64 * 1024 * 1024 * 1024L, 100, 100, 0));
    Map<UserIdentifier, ResourceConsumption> userResourceConsumption3 =
        JavaUtils.newConcurrentHashMap();
    userResourceConsumption3.put(
        new UserIdentifier("tenant3", "name1"), new ResourceConsumption(1000, 1, 1000, 1));
    userResourceConsumption3.put(
        new UserIdentifier("tenant3", "name2"), new ResourceConsumption(2000, 2, 2000, 2));
    userResourceConsumption3.put(
        new UserIdentifier("tenant3", "name3"), new ResourceConsumption(3000, 3, 3000, 3));

    WorkerInfo info1 = new WorkerInfo("host1", 1, 2, 3, 10, disks1, userResourceConsumption1);
    WorkerInfo info2 = new WorkerInfo("host2", 4, 5, 6, 11, disks2, userResourceConsumption2);
    WorkerInfo info3 = new WorkerInfo("host3", 7, 8, 9, 12, disks3, userResourceConsumption3);

    String host1 = "host1";
    String host2 = "host2";
    String host3 = "host3";

    masterStatusSystem.excludedWorkers.add(info1);
    masterStatusSystem.excludedWorkers.add(info2);
    masterStatusSystem.excludedWorkers.add(info3);

    masterStatusSystem.hostnameSet.add(host1);
    masterStatusSystem.hostnameSet.add(host2);
    masterStatusSystem.hostnameSet.add(host3);

    // Wait for update snapshot
    Thread.sleep(60000);
    Map<String, Long> appDiskUsage = JavaUtils.newConcurrentHashMap();
    appDiskUsage.put("app-1", 100L);
    appDiskUsage.put("app-2", 200L);
    masterStatusSystem.appDiskUsageMetric.update(appDiskUsage);
    appDiskUsage.put("app-3", 300L);
    appDiskUsage.put("app-1", 200L);
    masterStatusSystem.appDiskUsageMetric.update(appDiskUsage);
    // wait for snapshot updated
    Thread.sleep(3000);

    AppDiskUsageSnapShot[] originSnapshots = masterStatusSystem.appDiskUsageMetric.snapShots();
    AppDiskUsageSnapShot originCurrentSnapshot =
        masterStatusSystem.appDiskUsageMetric.currentSnapShot().get();

    masterStatusSystem.workers.add(new WorkerInfo(host1, 9095, 9094, 9093, 9092));
    masterStatusSystem.workers.add(new WorkerInfo(host2, 9095, 9094, 9093, 9092));
    masterStatusSystem.workers.add(new WorkerInfo(host3, 9095, 9094, 9093, 9092));

    masterStatusSystem.writeMetaInfoToFile(tmpFile);

    masterStatusSystem.hostnameSet.clear();
    masterStatusSystem.excludedWorkers.clear();
    masterStatusSystem.workers.clear();

    masterStatusSystem.restoreMetaFromFile(tmpFile);

    Assert.assertEquals(3, masterStatusSystem.workers.size());
    Assert.assertEquals(3, masterStatusSystem.excludedWorkers.size());
    Assert.assertEquals(3, masterStatusSystem.hostnameSet.size());
    Assert.assertEquals(
        conf.metricsAppTopDiskUsageWindowSize(),
        masterStatusSystem.appDiskUsageMetric.snapShots().length);
    Assert.assertEquals(
        conf.metricsAppTopDiskUsageCount(),
        masterStatusSystem.appDiskUsageMetric.currentSnapShot().get().topNItems().length);
    Assert.assertEquals(
        originCurrentSnapshot, masterStatusSystem.appDiskUsageMetric.currentSnapShot().get());
    Assert.assertArrayEquals(originSnapshots, masterStatusSystem.appDiskUsageMetric.snapShots());

    masterStatusSystem.restoreMetaFromFile(tmpFile);
    Assert.assertEquals(3, masterStatusSystem.workers.size());
  }
}
