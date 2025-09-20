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

package org.apache.celeborn.service.deploy.worker

import org.apache.celeborn.common.CelebornConf
import org.apache.celeborn.common.metrics.MetricsSystem
import org.apache.celeborn.common.metrics.source.AbstractSource

class WorkerSource(conf: CelebornConf) extends AbstractSource(conf, MetricsSystem.ROLE_WORKER) {
  override val sourceName = "worker"

  import WorkerSource._
  // add counters
  addCounter(OPEN_STREAM_SUCCESS_COUNT)
  addCounter(OPEN_STREAM_FAIL_COUNT)
  addCounter(FETCH_CHUNK_SUCCESS_COUNT)
  addCounter(FETCH_CHUNK_FAIL_COUNT)
  addCounter(WRITE_DATA_HARD_SPLIT_COUNT)
  addCounter(WRITE_DATA_SUCCESS_COUNT)
  addCounter(WRITE_DATA_FAIL_COUNT)
  addCounter(REPLICATE_DATA_FAIL_COUNT)
  addCounter(REPLICATE_DATA_WRITE_FAIL_COUNT)
  addCounter(REPLICATE_DATA_CREATE_CONNECTION_FAIL_COUNT)
  addCounter(REPLICATE_DATA_CONNECTION_EXCEPTION_COUNT)
  addCounter(REPLICATE_DATA_TIMEOUT_COUNT)

  addCounter(PUSH_DATA_HANDSHAKE_FAIL_COUNT)
  addCounter(REGION_START_FAIL_COUNT)
  addCounter(REGION_FINISH_FAIL_COUNT)
  addCounter(ACTIVE_CONNECTION_COUNT)

  addCounter(SLOTS_ALLOCATED)

  // add timers
  addTimer(COMMIT_FILES_TIME)
  addTimer(RESERVE_SLOTS_TIME)
  addTimer(FLUSH_DATA_TIME)
  addTimer(PRIMARY_PUSH_DATA_TIME)
  addTimer(REPLICA_PUSH_DATA_TIME)

  addTimer(PRIMARY_PUSH_DATA_HANDSHAKE_TIME)
  addTimer(REPLICA_PUSH_DATA_HANDSHAKE_TIME)
  addTimer(PRIMARY_REGION_START_TIME)
  addTimer(REPLICA_REGION_START_TIME)
  addTimer(PRIMARY_REGION_FINISH_TIME)
  addTimer(REPLICA_REGION_FINISH_TIME)

  addTimer(FETCH_CHUNK_TIME)
  addTimer(OPEN_STREAM_TIME)
  addTimer(TAKE_BUFFER_TIME)
  addTimer(SORT_TIME)

  def getCounterCount(metricsName: String): Long = {
    val metricNameWithLabel = metricNameWithCustomizedLabels(metricsName, Map.empty)
    namedCounters.get(metricNameWithLabel).counter.getCount
  }
  // start cleaner thread
  startCleaner()
}

object WorkerSource {
  val REGISTERED_SHUFFLE_COUNT = "RegisteredShuffleCount"

  val RUNNING_APPLICATION_COUNT = "RunningApplicationCount"

  // fetch data
  val OPEN_STREAM_TIME = "OpenStreamTime"
  val FETCH_CHUNK_TIME = "FetchChunkTime"
  val CHUNK_STREAM_COUNT = "ChunkStreamCount"
  val OPEN_STREAM_SUCCESS_COUNT = "OpenStreamSuccessCount"
  val OPEN_STREAM_FAIL_COUNT = "OpenStreamFailCount"
  val FETCH_CHUNK_SUCCESS_COUNT = "FetchChunkSuccessCount"
  val FETCH_CHUNK_FAIL_COUNT = "FetchChunkFailCount"

  // push data
  val PRIMARY_PUSH_DATA_TIME = "PrimaryPushDataTime"
  val REPLICA_PUSH_DATA_TIME = "ReplicaPushDataTime"
  val WRITE_DATA_HARD_SPLIT_COUNT = "WriteDataHardSplitCount"
  val WRITE_DATA_SUCCESS_COUNT = "WriteDataSuccessCount"
  val WRITE_DATA_FAIL_COUNT = "WriteDataFailCount"
  val REPLICATE_DATA_FAIL_COUNT = "ReplicateDataFailCount"
  val REPLICATE_DATA_WRITE_FAIL_COUNT = "ReplicateDataWriteFailCount"
  val REPLICATE_DATA_CREATE_CONNECTION_FAIL_COUNT = "ReplicateDataCreateConnectionFailCount"
  val REPLICATE_DATA_CONNECTION_EXCEPTION_COUNT = "ReplicateDataConnectionExceptionCount"
  val REPLICATE_DATA_TIMEOUT_COUNT = "ReplicateDataTimeoutCount"
  val PUSH_DATA_HANDSHAKE_FAIL_COUNT = "PushDataHandshakeFailCount"
  val REGION_START_FAIL_COUNT = "RegionStartFailCount"
  val REGION_FINISH_FAIL_COUNT = "RegionFinishFailCount"
  val PRIMARY_PUSH_DATA_HANDSHAKE_TIME = "PrimaryPushDataHandshakeTime"
  val REPLICA_PUSH_DATA_HANDSHAKE_TIME = "ReplicaPushDataHandshakeTime"
  val PRIMARY_REGION_START_TIME = "PrimaryRegionStartTime"
  val REPLICA_REGION_START_TIME = "ReplicaRegionStartTime"
  val PRIMARY_REGION_FINISH_TIME = "PrimaryRegionFinishTime"
  val REPLICA_REGION_FINISH_TIME = "ReplicaRegionFinishTime"

  // pause push data
  val PAUSE_PUSH_DATA_TIME = "PausePushDataTime"
  val PAUSE_PUSH_DATA_AND_REPLICATE_TIME = "PausePushDataAndReplicateTime"
  val PAUSE_PUSH_DATA_COUNT = "PausePushData"
  val PAUSE_PUSH_DATA_AND_REPLICATE_COUNT = "PausePushDataAndReplicate"

  // flush
  val TAKE_BUFFER_TIME = "TakeBufferTime"
  val FLUSH_DATA_TIME = "FlushDataTime"
  val COMMIT_FILES_TIME = "CommitFilesTime"

  // slots
  val SLOTS_ALLOCATED = "SlotsAllocated"
  val RESERVE_SLOTS_TIME = "ReserveSlotsTime"

  // connection
  val ACTIVE_CONNECTION_COUNT = "ActiveConnectionCount"

  // memory
  val NETTY_MEMORY = "NettyMemory"
  val SORT_TIME = "SortTime"
  val SORT_MEMORY = "SortMemory"
  val SORTING_FILES = "SortingFiles"
  val SORTED_FILES = "SortedFiles"
  val SORTED_FILE_SIZE = "SortedFileSize"
  val DISK_BUFFER = "DiskBuffer"
  val BUFFER_STREAM_READ_BUFFER = "BufferStreamReadBuffer"
  val READ_BUFFER_DISPATCHER_REQUESTS_LENGTH = "ReadBufferDispatcherRequestsLength"
  val READ_BUFFER_ALLOCATED_COUNT = "ReadBufferAllocatedCount"

  // credit
  val CREDIT_STREAM_COUNT = "CreditStreamCount"
  val ACTIVE_MAP_PARTITION_COUNT = "ActiveMapPartitionCount"

  // local device
  val DEVICE_OS_FREE_CAPACITY = "DeviceOSFreeBytes"
  val DEVICE_OS_TOTAL_CAPACITY = "DeviceOSTotalBytes"
  val DEVICE_CELEBORN_FREE_CAPACITY = "DeviceCelebornFreeBytes"
  val DEVICE_CELEBORN_TOTAL_CAPACITY = "DeviceCelebornTotalBytes"

  // congestion control
  val POTENTIAL_CONSUME_SPEED = "PotentialConsumeSpeed"
  val USER_PRODUCE_SPEED = "UserProduceSpeed"
  val WORKER_CONSUME_SPEED = "WorkerConsumeSpeed"

  // active shuffle
  val ACTIVE_SHUFFLE_SIZE = "ActiveShuffleSize"
  val ACTIVE_SHUFFLE_FILE_COUNT = "ActiveShuffleFileCount"
}
