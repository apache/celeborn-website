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
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import scala.Tuple2;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.ratis.RaftConfigKeys;
import org.apache.ratis.client.RaftClientConfigKeys;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.netty.NettyConfigKeys;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.*;
import org.apache.ratis.protocol.exceptions.RaftException;
import org.apache.ratis.rpc.CallId;
import org.apache.ratis.rpc.RpcType;
import org.apache.ratis.rpc.SupportedRpcType;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.LifeCycle;
import org.apache.ratis.util.SizeInBytes;
import org.apache.ratis.util.TimeDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.common.CelebornConf;
import org.apache.celeborn.common.client.MasterClient;
import org.apache.celeborn.common.exception.CelebornRuntimeException;
import org.apache.celeborn.common.util.ThreadUtils;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos;
import org.apache.celeborn.service.deploy.master.clustermeta.ResourceProtos.ResourceResponse;

public class HARaftServer {
  public static final Logger LOG = LoggerFactory.getLogger(HARaftServer.class);

  public static final UUID CELEBORN_UUID =
      UUID.nameUUIDFromBytes("CELEBORN".getBytes(StandardCharsets.UTF_8));

  public static final RaftGroupId RAFT_GROUP_ID = RaftGroupId.valueOf(CELEBORN_UUID);

  private static final AtomicLong CALL_ID_COUNTER = new AtomicLong();

  static long nextCallId() {
    return CALL_ID_COUNTER.getAndIncrement() & Long.MAX_VALUE;
  }

  private final InetSocketAddress ratisAddr;
  private final String rpcEndpoint;
  private final RaftServer server;
  private final RaftGroup raftGroup;
  private final RaftPeerId raftPeerId;

  private final ClientId clientId = ClientId.randomId();

  private final MetaHandler metaHandler;

  private final StateMachine masterStateMachine;

  private final ScheduledExecutorService scheduledRoleChecker =
      ThreadUtils.newDaemonSingleThreadScheduledExecutor("master-ratis-role-checker");
  private long roleCheckIntervalMs;
  private final ReentrantReadWriteLock roleCheckLock = new ReentrantReadWriteLock();
  private Optional<RaftProtos.RaftPeerRole> cachedPeerRole = Optional.empty();
  private Optional<String> cachedLeaderPeerRpcEndpoint = Optional.empty();
  private final CelebornConf conf;
  private long workerTimeoutDeadline;
  private long appTimeoutDeadline;

  /**
   * Returns a Master Ratis server.
   *
   * @param conf configuration
   * @param localRaftPeerId raft peer id of this Ratis server
   * @param ratisAddr address of the ratis server
   * @param raftPeers peer nodes in the raft ring
   * @throws IOException
   */
  private HARaftServer(
      MetaHandler metaHandler,
      CelebornConf conf,
      RaftPeerId localRaftPeerId,
      InetSocketAddress ratisAddr,
      String rpcEndpoint,
      List<RaftPeer> raftPeers)
      throws IOException {
    this.metaHandler = metaHandler;
    this.ratisAddr = ratisAddr;
    this.rpcEndpoint = rpcEndpoint;
    this.raftPeerId = localRaftPeerId;
    this.raftGroup = RaftGroup.valueOf(RAFT_GROUP_ID, raftPeers);
    this.masterStateMachine = getStateMachine();
    this.conf = conf;
    RaftProperties serverProperties = newRaftProperties(conf);
    setDeadlineTime(Integer.MAX_VALUE, Integer.MAX_VALUE); // for default
    this.server =
        RaftServer.newBuilder()
            .setServerId(this.raftPeerId)
            .setGroup(this.raftGroup)
            .setProperties(serverProperties)
            .setStateMachine(masterStateMachine)
            .build();

    StringBuilder raftPeersStr = new StringBuilder();
    for (RaftPeer peer : raftPeers) {
      raftPeersStr.append(", ").append(peer.getAddress());
    }
    LOG.info(
        "Ratis server started with GroupID: {} and Raft Peers: {}.",
        RAFT_GROUP_ID,
        raftPeersStr.substring(2));

    // Run a scheduler to check and update the server role on the leader periodically
    this.scheduledRoleChecker.scheduleWithFixedDelay(
        () -> {
          // Run this check only on the leader OM
          if (cachedPeerRole.isPresent()
              && cachedPeerRole.get() == RaftProtos.RaftPeerRole.LEADER) {
            updateServerRole();
          }
        },
        roleCheckIntervalMs,
        roleCheckIntervalMs,
        TimeUnit.MILLISECONDS);
  }

  public static HARaftServer newMasterRatisServer(
      MetaHandler metaHandler, CelebornConf conf, MasterNode localNode, List<MasterNode> peerNodes)
      throws IOException {
    String nodeId = localNode.nodeId();
    RaftPeerId localRaftPeerId = RaftPeerId.getRaftPeerId(nodeId);

    InetSocketAddress ratisAddr = localNode.ratisAddr();
    RaftPeer localRaftPeer =
        RaftPeer.newBuilder()
            .setId(localRaftPeerId)
            .setAddress(ratisAddr)
            .setClientAddress(localNode.rpcEndpoint())
            .build();
    List<RaftPeer> raftPeers = new ArrayList<>();
    // Add this Ratis server to the Ratis ring
    raftPeers.add(localRaftPeer);
    peerNodes.forEach(
        peer -> {
          String peerNodeId = peer.nodeId();
          RaftPeerId raftPeerId = RaftPeerId.valueOf(peerNodeId);
          RaftPeer raftPeer;
          if (peer.isRatisHostUnresolved()) {
            raftPeer =
                RaftPeer.newBuilder()
                    .setId(raftPeerId)
                    .setAddress(peer.ratisEndpoint())
                    .setClientAddress(peer.rpcEndpoint())
                    .build();
          } else {
            InetSocketAddress peerRatisAddr = peer.ratisAddr();
            raftPeer =
                RaftPeer.newBuilder()
                    .setId(raftPeerId)
                    .setAddress(peerRatisAddr)
                    .setClientAddress(peer.rpcEndpoint())
                    .build();
          }

          // Add other nodes belonging to the same service to the Ratis ring
          raftPeers.add(raftPeer);
        });
    return new HARaftServer(
        metaHandler, conf, localRaftPeerId, ratisAddr, localNode.rpcEndpoint(), raftPeers);
  }

  public ResourceResponse submitRequest(ResourceProtos.ResourceRequest request)
      throws CelebornRuntimeException {
    String requestId = request.getRequestId();
    Tuple2<String, Long> decoded = MasterClient.decodeRequestId(requestId);
    if (decoded == null) {
      throw new CelebornRuntimeException(
          "RequestId:" + requestId + " invalid, should be: uuid#callId.");
    }
    ClientId clientId = ClientId.valueOf(UUID.fromString(decoded._1));
    long callId = decoded._2;
    RaftClientRequest raftClientRequest =
        new RaftClientRequest.Builder()
            .setClientId(clientId)
            .setServerId(server.getId())
            .setGroupId(RAFT_GROUP_ID)
            .setCallId(callId)
            .setType(RaftClientRequest.writeRequestType())
            .setMessage(Message.valueOf(HAHelper.convertRequestToByteString(request)))
            .build();

    RaftClientReply raftClientReply;
    try {
      raftClientReply = server.submitClientRequestAsync(raftClientRequest).get();
    } catch (Exception ex) {
      throw new CelebornRuntimeException(ex.getMessage(), ex);
    }

    if (!raftClientReply.isSuccess()) {
      RaftException exception = raftClientReply.getException();
      throw new CelebornRuntimeException(
          exception == null ? "Encounter raft exception." : exception.getMessage());
    }

    try {
      byte[] bytes = raftClientReply.getMessage().getContent().toByteArray();
      return ResourceResponse.newBuilder(ResourceResponse.parseFrom(bytes)).build();
    } catch (InvalidProtocolBufferException ex) {
      throw new CelebornRuntimeException(ex.getMessage(), ex);
    }
  }

  /**
   * Start the Ratis server.
   *
   * @throws IOException
   */
  public void start() throws IOException {
    LOG.info(
        "Starting {} {} at port {}",
        getClass().getSimpleName(),
        server.getId(),
        ratisAddr.getPort());
    server.start();
  }

  public void stop() {
    try {
      server.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private RaftProperties newRaftProperties(CelebornConf conf) {
    final RaftProperties properties = new RaftProperties();
    // Set RPC type
    final String rpcType = conf.haMasterRatisRpcType();
    final RpcType rpc = SupportedRpcType.valueOfIgnoreCase(rpcType);
    RaftConfigKeys.Rpc.setType(properties, rpc);

    // Set the ratis port number
    if (rpc == SupportedRpcType.GRPC) {
      GrpcConfigKeys.Server.setPort(properties, ratisAddr.getPort());
    } else if (rpc == SupportedRpcType.NETTY) {
      NettyConfigKeys.Server.setPort(properties, ratisAddr.getPort());
    }

    // Set Ratis storage directory
    String storageDir = conf.haMasterRatisStorageDir();
    RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(new File(storageDir)));

    // Set RAFT segment size
    long raftSegmentSize = conf.haMasterRatisLogSegmentSizeMax();
    RaftServerConfigKeys.Log.setSegmentSizeMax(properties, SizeInBytes.valueOf(raftSegmentSize));
    RaftServerConfigKeys.Log.setPurgeUptoSnapshotIndex(properties, true);

    // Set RAFT segment pre-allocated size
    long raftSegmentPreallocatedSize = conf.haMasterRatisLogPreallocatedSize();
    int logAppenderQueueNumElements = conf.haMasterRatisLogAppenderQueueNumElements();
    long logAppenderQueueByteLimit = conf.haMasterRatisLogAppenderQueueBytesLimit();
    boolean shouldInstallSnapshot = conf.haMasterRatisLogInstallSnapshotEnabled();
    RaftServerConfigKeys.Log.Appender.setBufferElementLimit(
        properties, logAppenderQueueNumElements);
    RaftServerConfigKeys.Log.Appender.setBufferByteLimit(
        properties, SizeInBytes.valueOf(logAppenderQueueByteLimit));
    RaftServerConfigKeys.Log.setPreallocatedSize(
        properties, SizeInBytes.valueOf(raftSegmentPreallocatedSize));
    RaftServerConfigKeys.Log.Appender.setInstallSnapshotEnabled(properties, shouldInstallSnapshot);
    int logPurgeGap = conf.haMasterRatisLogPurgeGap();
    RaftServerConfigKeys.Log.setPurgeGap(properties, logPurgeGap);

    // For grpc set the maximum message size
    GrpcConfigKeys.setMessageSizeMax(properties, SizeInBytes.valueOf(logAppenderQueueByteLimit));

    // Set the server request timeout
    TimeDuration serverRequestTimeout =
        TimeDuration.valueOf(conf.haMasterRatisRpcRequestTimeout(), TimeUnit.SECONDS);
    RaftServerConfigKeys.Rpc.setRequestTimeout(properties, serverRequestTimeout);

    // Set timeout for server retry cache entry
    TimeDuration retryCacheExpiryTime =
        TimeDuration.valueOf(conf.haMasterRatisRetryCacheExpiryTime(), TimeUnit.SECONDS);
    RaftServerConfigKeys.RetryCache.setExpiryTime(properties, retryCacheExpiryTime);

    // Set the server min and max timeout
    TimeDuration rpcTimeoutMin =
        TimeDuration.valueOf(conf.haMasterRatisRpcTimeoutMin(), TimeUnit.SECONDS);
    TimeDuration rpcTimeoutMax =
        TimeDuration.valueOf(conf.haMasterRatisRpcTimeoutMax(), TimeUnit.SECONDS);
    RaftServerConfigKeys.Rpc.setTimeoutMin(properties, rpcTimeoutMin);
    RaftServerConfigKeys.Rpc.setTimeoutMax(properties, rpcTimeoutMax);

    TimeDuration firstElectionTimeoutMin =
        TimeDuration.valueOf(conf.haMasterRatisFirstElectionTimeoutMin(), TimeUnit.SECONDS);
    TimeDuration firstElectionTimeoutMax =
        TimeDuration.valueOf(conf.haMasterRatisFirstElectionTimeoutMax(), TimeUnit.SECONDS);
    RaftServerConfigKeys.Rpc.setFirstElectionTimeoutMin(properties, firstElectionTimeoutMin);
    RaftServerConfigKeys.Rpc.setFirstElectionTimeoutMax(properties, firstElectionTimeoutMax);

    // Set the rpc client timeout
    TimeDuration clientRpcTimeout =
        TimeDuration.valueOf(conf.haMasterRatisClientRpcTimeout(), TimeUnit.SECONDS);
    TimeDuration clientRpcWatchTimeout =
        TimeDuration.valueOf(conf.haMasterRatisClientRpcWatchTimeout(), TimeUnit.SECONDS);
    RaftClientConfigKeys.Rpc.setRequestTimeout(properties, clientRpcTimeout);
    RaftClientConfigKeys.Rpc.setWatchRequestTimeout(properties, clientRpcWatchTimeout);

    // Set the number of maximum cached segments
    RaftServerConfigKeys.Log.setSegmentCacheNumMax(properties, 2);

    TimeDuration noLeaderTimeout =
        TimeDuration.valueOf(conf.haMasterRatisNotificationNoLeaderTimeout(), TimeUnit.SECONDS);
    RaftServerConfigKeys.Notification.setNoLeaderTimeout(properties, noLeaderTimeout);
    TimeDuration slownessTimeout =
        TimeDuration.valueOf(conf.haMasterRatisRpcSlownessTimeout(), TimeUnit.SECONDS);
    RaftServerConfigKeys.Rpc.setSlownessTimeout(properties, slownessTimeout);

    // Set role checker time
    this.roleCheckIntervalMs = conf.haMasterRatisRoleCheckInterval();

    // snapshot retention
    int numSnapshotRetentionFileNum = conf.haMasterRatisSnapshotRetentionFileNum();
    RaftServerConfigKeys.Snapshot.setRetentionFileNum(properties, numSnapshotRetentionFileNum);

    // snapshot interval
    RaftServerConfigKeys.Snapshot.setAutoTriggerEnabled(
        properties, conf.haMasterRatisSnapshotAutoTriggerEnabled());

    long snapshotAutoTriggerThreshold = conf.haMasterRatisSnapshotAutoTriggerThreshold();
    RaftServerConfigKeys.Snapshot.setAutoTriggerThreshold(properties, snapshotAutoTriggerThreshold);

    for (Map.Entry<String, String> ratisEntry : conf.haRatisCustomConfigs().entrySet()) {
      properties.set(ratisEntry.getKey().replace("celeborn.ratis.", ""), ratisEntry.getValue());
    }

    return properties;
  }

  private StateMachine getStateMachine() {
    StateMachine stateMachine = new StateMachine(this);
    stateMachine.setRaftGroupId(RAFT_GROUP_ID);
    return stateMachine;
  }

  public MetaHandler getMetaHandler() {
    return metaHandler;
  }

  @VisibleForTesting
  public LifeCycle.State getServerState() {
    return server.getLifeCycleState();
  }

  public RaftGroup getRaftGroup() {
    return this.raftGroup;
  }

  public StateMachine getMasterStateMachine() {
    return this.masterStateMachine;
  }

  /**
   * Check the cached leader status.
   *
   * @return true if cached role is Leader, false otherwise.
   */
  private boolean checkCachedPeerRoleIsLeader() {
    this.roleCheckLock.readLock().lock();
    try {
      return cachedPeerRole.isPresent() && cachedPeerRole.get() == RaftProtos.RaftPeerRole.LEADER;
    } finally {
      this.roleCheckLock.readLock().unlock();
    }
  }

  /**
   * Check if the current node is the leader node.
   *
   * @return true if Leader, false otherwise.
   */
  public boolean isLeader() {
    if (checkCachedPeerRoleIsLeader()) {
      return true;
    }

    // Get the server role from ratis server and update the cached values.
    updateServerRole();

    // After updating the server role, check and return if leader or not.
    return checkCachedPeerRoleIsLeader();
  }

  /**
   * Get the suggested leader peer id.
   *
   * @return RaftPeerId of the suggested leader node.
   */
  public Optional<String> getCachedLeaderPeerRpcEndpoint() {
    this.roleCheckLock.readLock().lock();
    try {
      return cachedLeaderPeerRpcEndpoint;
    } finally {
      this.roleCheckLock.readLock().unlock();
    }
  }

  /**
   * Get the group info (peer role and leader peer id) from Ratis server and update the server role.
   */
  public void updateServerRole() {
    try {
      GroupInfoReply groupInfo = getGroupInfo();
      RaftProtos.RoleInfoProto roleInfoProto = groupInfo.getRoleInfoProto();
      RaftProtos.RaftPeerRole thisNodeRole = roleInfoProto.getRole();

      if (thisNodeRole.equals(RaftProtos.RaftPeerRole.LEADER)) {
        setServerRole(thisNodeRole, getRpcEndpoint());
      } else if (thisNodeRole.equals(RaftProtos.RaftPeerRole.FOLLOWER)) {
        ByteString leaderNodeId = roleInfoProto.getFollowerInfo().getLeaderInfo().getId().getId();
        // There may be a chance, here we get leaderNodeId as null. For
        // example, in 3 node Ratis, if 2 nodes are down, there will
        // be no leader.
        String leaderPeerRpcEndpoint = null;
        if (leaderNodeId != null && !leaderNodeId.isEmpty()) {
          leaderPeerRpcEndpoint =
              roleInfoProto.getFollowerInfo().getLeaderInfo().getId().getClientAddress();
        }

        setServerRole(thisNodeRole, leaderPeerRpcEndpoint);

      } else {
        setServerRole(thisNodeRole, null);
      }
    } catch (IOException e) {
      LOG.error(
          "Failed to retrieve RaftPeerRole. Setting cached role to "
              + "{} and resetting leader info.",
          RaftProtos.RaftPeerRole.UNRECOGNIZED,
          e);
      setServerRole(null, null);
    }
  }

  /** Set the current server role and the leader peer rpc endpoint. */
  private void setServerRole(RaftProtos.RaftPeerRole currentRole, String leaderPeerRpcEndpoint) {
    this.roleCheckLock.writeLock().lock();
    try {
      boolean leaderChanged = false;
      if (RaftProtos.RaftPeerRole.LEADER == currentRole && !checkCachedPeerRoleIsLeader()) {
        leaderChanged = true;
        setDeadlineTime(conf.workerHeartbeatTimeout(), conf.appHeartbeatTimeoutMs());
      } else if (RaftProtos.RaftPeerRole.LEADER != currentRole && checkCachedPeerRoleIsLeader()) {
        leaderChanged = true;
        setDeadlineTime(Integer.MAX_VALUE, Integer.MAX_VALUE); // for revoke
      }

      if (leaderChanged) {
        LOG.warn(
            "Raft Role changed, CurrentNode Role: {}, Leader: {}",
            currentRole,
            leaderPeerRpcEndpoint);
      }

      this.cachedPeerRole = Optional.ofNullable(currentRole);
      this.cachedLeaderPeerRpcEndpoint = Optional.ofNullable(leaderPeerRpcEndpoint);
    } finally {
      this.roleCheckLock.writeLock().unlock();
    }
  }

  public GroupInfoReply getGroupInfo() throws IOException {
    GroupInfoRequest groupInfoRequest =
        new GroupInfoRequest(clientId, raftPeerId, RAFT_GROUP_ID, nextCallId());
    return server.getGroupInfo(groupInfoRequest);
  }

  public int getRaftPort() {
    return this.ratisAddr.getPort();
  }

  public String getRpcEndpoint() {
    return this.rpcEndpoint;
  }

  void stepDown() {
    try {
      TransferLeadershipRequest request =
          new TransferLeadershipRequest(
              clientId,
              server.getId(),
              raftGroup.getGroupId(),
              CallId.getAndIncrement(),
              null,
              60_000);
      RaftClientReply reply = server.transferLeadership(request);
      if (reply.isSuccess()) {
        LOG.info("Successfully step down leader {}.", server.getId());
      } else {
        LOG.warn("Step down leader failed!");
      }
    } catch (Exception e) {
      LOG.warn("Step down leader failed!", e);
    }
  }

  public void setDeadlineTime(long increaseWorkerTime, long increaseAppTime) {
    this.workerTimeoutDeadline = System.currentTimeMillis() + increaseWorkerTime;
    this.appTimeoutDeadline = System.currentTimeMillis() + increaseAppTime;
  }

  public long getWorkerTimeoutDeadline() {
    return workerTimeoutDeadline;
  }

  public long getAppTimeoutDeadline() {
    return appTimeoutDeadline;
  }
}
