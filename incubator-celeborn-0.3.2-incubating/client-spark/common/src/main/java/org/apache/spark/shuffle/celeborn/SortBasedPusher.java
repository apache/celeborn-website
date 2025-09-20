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

package org.apache.spark.shuffle.celeborn;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

import org.apache.spark.TaskContext;
import org.apache.spark.memory.MemoryConsumer;
import org.apache.spark.memory.SparkOutOfMemoryError;
import org.apache.spark.memory.TaskMemoryManager;
import org.apache.spark.memory.TooLargePageException;
import org.apache.spark.unsafe.Platform;
import org.apache.spark.unsafe.UnsafeAlignedOffset;
import org.apache.spark.unsafe.array.LongArray;
import org.apache.spark.unsafe.memory.MemoryBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.celeborn.client.ShuffleClient;
import org.apache.celeborn.client.write.DataPusher;
import org.apache.celeborn.client.write.PushTask;
import org.apache.celeborn.common.CelebornConf;
import org.apache.celeborn.common.util.JavaUtils;
import org.apache.celeborn.common.util.Utils;

public class SortBasedPusher extends MemoryConsumer {

  private static final Logger logger = LoggerFactory.getLogger(SortBasedPusher.class);

  private static final int UAO_SIZE = UnsafeAlignedOffset.getUaoSize();

  /** Peak memory used by this sorter so far, in bytes. * */
  private long peakMemoryUsedBytes;

  private ShuffleInMemorySorter inMemSorter;
  private final LinkedList<MemoryBlock> allocatedPages = new LinkedList<>();
  private MemoryBlock currentPage = null;
  private long pageCursor = -1;

  private final ShuffleClient shuffleClient;
  private final TaskContext taskContext;
  private DataPusher dataPusher;
  private final int pushBufferMaxSize;
  private final long pushSortMemoryThreshold;
  private final int shuffleId;
  private final int mapId;
  private final int attemptNumber;
  private final int numMappers;
  private final int numPartitions;
  private final Consumer<Integer> afterPush;
  private final LongAdder[] mapStatusLengths;
  // this lock is shared between different SortBasedPushers to synchronize pushData
  private final Object sharedPushLock;
  private volatile boolean asyncPushing = false;
  private int[] shuffledPartitions = null;
  private int[] inversedShuffledPartitions = null;
  private final ExecutorService executorService;
  private final SendBufferPool sendBufferPool;

  public SortBasedPusher(
      TaskMemoryManager memoryManager,
      ShuffleClient shuffleClient,
      TaskContext taskContext,
      int shuffleId,
      int mapId,
      int attemptNumber,
      long taskAttemptId,
      int numMappers,
      int numPartitions,
      CelebornConf conf,
      Consumer<Integer> afterPush,
      LongAdder[] mapStatusLengths,
      long pushSortMemoryThreshold,
      Object sharedPushLock,
      ExecutorService executorService,
      SendBufferPool sendBufferPool) {
    super(
        memoryManager,
        (int) Math.min(PackedRecordPointer.MAXIMUM_PAGE_SIZE_BYTES, memoryManager.pageSizeBytes()),
        memoryManager.getTungstenMemoryMode());

    this.shuffleClient = shuffleClient;
    this.taskContext = taskContext;

    this.shuffleId = shuffleId;
    this.mapId = mapId;
    this.attemptNumber = attemptNumber;
    this.numMappers = numMappers;
    this.numPartitions = numPartitions;

    if (conf.clientPushSortRandomizePartitionIdEnabled()) {
      shuffledPartitions = new int[numPartitions];
      inversedShuffledPartitions = new int[numPartitions];
      JavaUtils.shuffleArray(shuffledPartitions, inversedShuffledPartitions);
    }

    this.afterPush = afterPush;
    this.mapStatusLengths = mapStatusLengths;

    this.sendBufferPool = sendBufferPool;

    try {
      LinkedBlockingQueue<PushTask> pushTaskQueue = sendBufferPool.acquirePushTaskQueue();
      dataPusher =
          new DataPusher(
              shuffleId,
              mapId,
              attemptNumber,
              taskAttemptId,
              numMappers,
              numPartitions,
              conf,
              shuffleClient,
              pushTaskQueue,
              afterPush,
              mapStatusLengths);
    } catch (InterruptedException e) {
      TaskInterruptedHelper.throwTaskKillException();
    }

    pushBufferMaxSize = conf.clientPushBufferMaxSize();
    this.pushSortMemoryThreshold = pushSortMemoryThreshold;

    int initialSize = Math.min((int) pushSortMemoryThreshold / 8, 1024 * 1024);
    this.inMemSorter = new ShuffleInMemorySorter(this, initialSize);
    this.peakMemoryUsedBytes = getMemoryUsage();
    this.sharedPushLock = sharedPushLock;
    this.executorService = executorService;
  }

  public long pushData() throws IOException {
    // pushData should be synchronized between pushers
    synchronized (sharedPushLock) {
      final ShuffleInMemorySorter.ShuffleSorterIterator sortedRecords =
          inMemSorter.getSortedIterator();

      byte[] dataBuf = new byte[pushBufferMaxSize];
      int offSet = 0;
      int currentPartition = -1;
      while (sortedRecords.hasNext()) {
        sortedRecords.loadNext();
        final int partition =
            shuffledPartitions != null
                ? inversedShuffledPartitions[sortedRecords.packedRecordPointer.getPartitionId()]
                : sortedRecords.packedRecordPointer.getPartitionId();
        if (partition != currentPartition) {
          if (currentPartition == -1) {
            currentPartition = partition;
          } else {
            int bytesWritten =
                shuffleClient.mergeData(
                    shuffleId,
                    mapId,
                    attemptNumber,
                    currentPartition,
                    dataBuf,
                    0,
                    offSet,
                    numMappers,
                    numPartitions);
            mapStatusLengths[currentPartition].add(bytesWritten);
            afterPush.accept(bytesWritten);
            currentPartition = partition;
            offSet = 0;
          }
        }
        final long recordPointer = sortedRecords.packedRecordPointer.getRecordPointer();
        final Object recordPage = taskMemoryManager.getPage(recordPointer);
        final long recordOffsetInPage = taskMemoryManager.getOffsetInPage(recordPointer);
        int recordSize = UnsafeAlignedOffset.getSize(recordPage, recordOffsetInPage);

        if (offSet + recordSize > dataBuf.length) {
          try {
            dataPusher.addTask(partition, dataBuf, offSet);
          } catch (InterruptedException e) {
            TaskInterruptedHelper.throwTaskKillException();
          }
          offSet = 0;
        }

        long recordReadPosition = recordOffsetInPage + UAO_SIZE;
        Platform.copyMemory(
            recordPage,
            recordReadPosition,
            dataBuf,
            Platform.BYTE_ARRAY_OFFSET + offSet,
            recordSize);
        offSet += recordSize;
      }
      if (offSet > 0) {
        try {
          dataPusher.addTask(currentPartition, dataBuf, offSet);
        } catch (InterruptedException e) {
          TaskInterruptedHelper.throwTaskKillException();
        }
      }

      long freedBytes = freeMemory();
      inMemSorter.freeMemory();
      taskContext.taskMetrics().incMemoryBytesSpilled(freedBytes);

      return freedBytes;
    }
  }

  public boolean insertRecord(
      Object recordBase, long recordOffset, int recordSize, int partitionId, boolean copySize)
      throws IOException {
    int required;
    // Need 4 or 8 bytes to store the record recordSize.
    if (copySize) {
      required = recordSize + 4 + UAO_SIZE;
    } else {
      required = recordSize + UAO_SIZE;
    }

    if (getUsed() > pushSortMemoryThreshold
        && pageCursor + required > currentPage.getBaseOffset() + currentPage.size()) {
      logger.info(
          "Memory used {} exceeds threshold {}, need to trigger push. currentPage size: {}",
          Utils.bytesToString(getUsed()),
          Utils.bytesToString(pushSortMemoryThreshold),
          Utils.bytesToString(currentPage.size()));
      return false;
    }

    allocateMemoryForRecordIfNecessary(required);

    assert (currentPage != null);
    final Object base = currentPage.getBaseObject();
    final long recordAddress = taskMemoryManager.encodePageNumberAndOffset(currentPage, pageCursor);
    if (copySize) {
      UnsafeAlignedOffset.putSize(base, pageCursor, recordSize + 4);
      pageCursor += UAO_SIZE;
      Platform.putInt(base, pageCursor, Integer.reverseBytes(recordSize));
      pageCursor += 4;
      Platform.copyMemory(recordBase, recordOffset, base, pageCursor, recordSize);
      pageCursor += recordSize;
    } else {
      UnsafeAlignedOffset.putSize(base, pageCursor, recordSize);
      pageCursor += UAO_SIZE;
      Platform.copyMemory(recordBase, recordOffset, base, pageCursor, recordSize);
      pageCursor += recordSize;
    }
    if (shuffledPartitions != null) {
      inMemSorter.insertRecord(recordAddress, shuffledPartitions[partitionId]);
    } else {
      inMemSorter.insertRecord(recordAddress, partitionId);
    }

    return true;
  }

  public void triggerPush() throws IOException {
    asyncPushing = true;
    dataPusher.checkException();
    executorService.submit(
        () -> {
          try {
            pushData();
            asyncPushing = false;
          } catch (IOException ie) {
            dataPusher.setException(ie);
          }
        });
  }

  /**
   * Since this method and pushData() are synchronized When this method returns, it means pushData
   * has released lock
   *
   * @throws IOException
   */
  public void waitPushFinish() throws IOException {
    dataPusher.checkException();
    while (asyncPushing) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        logger.error("SortBasedPusher thread interrupted while waiting push finished.");
        TaskInterruptedHelper.throwTaskKillException();
      }
    }
  }

  private void growPointerArrayIfNecessary() throws IOException {
    assert (inMemSorter != null);
    if (!inMemSorter.hasSpaceForAnotherRecord()) {
      if (inMemSorter.numRecords() <= 0) {
        // Spilling was triggered just before this method was called. The pointer array was freed
        // during the spill, so a new pointer array needs to be allocated here.
        LongArray array = allocateArray(inMemSorter.getInitialSize());
        inMemSorter.expandPointerArray(array);
        return;
      }

      long used = inMemSorter.getMemoryUsage();
      LongArray array = null;
      try {
        // could trigger spilling
        array = allocateArray(used / 8 * 2);
      } catch (TooLargePageException e) {
        // The pointer array is too big to fix in a single page, spill.
        logger.info(
            "Pushdata in growPointerArrayIfNecessary, memory used {}",
            Utils.bytesToString(getUsed()));
        pushData();
      } catch (SparkOutOfMemoryError rethrow) {
        // should have trigger spilling
        if (inMemSorter.numRecords() > 0) {
          logger.error("OOM, unable to grow the pointer array");
          throw rethrow;
        }
        // The new array could not be allocated, but that is not an issue as it is longer needed,
        // as all records were spilled.
      }

      if (inMemSorter.numRecords() <= 0) {
        // Spilling was triggered while trying to allocate the new array.
        if (array != null) {
          // We succeeded in allocating the new array, but, since all records were spilled, a
          // smaller array would also suffice.
          freeArray(array);
        }
        // The pointer array was freed during the spill, so a new pointer array needs to be
        // allocated here.
        array = allocateArray(inMemSorter.getInitialSize());
      }
      inMemSorter.expandPointerArray(array);
    }
  }

  /**
   * Allocates an additional page in order to insert an additional record. This will request
   * additional memory from the memory manager and spill if the requested memory can not be
   * obtained.
   *
   * @param required the required space in the data page, in bytes, including space for storing the
   *     record size.
   */
  private void acquireNewPageIfNecessary(int required) {
    if (currentPage == null
        || pageCursor + required > currentPage.getBaseOffset() + currentPage.size()) {
      currentPage = allocatePage(required);
      pageCursor = currentPage.getBaseOffset();
      allocatedPages.add(currentPage);
    }
  }

  /**
   * Allocates more memory in order to insert an additional record. This will request additional
   * memory from the memory manager and spill if the requested memory can not be obtained.
   *
   * @param required the required space in the data page, in bytes, including space for storing the
   *     record size.
   */
  private void allocateMemoryForRecordIfNecessary(int required) throws IOException {
    // Step 1:
    // Ensure that the pointer array has space for another record. This may cause a spill.
    growPointerArrayIfNecessary();
    // Step 2:
    // Ensure that the last page has space for another record. This may cause a spill.
    acquireNewPageIfNecessary(required);
    // Step 3:
    // The allocation in step 2 could have caused a spill, which would have freed the pointer
    // array allocated in step 1. Therefore we need to check again whether we have to allocate
    // a new pointer array.
    //
    // If the allocation in this step causes a spill event then it will not cause the page
    // allocated in the previous step to be freed. The function `spill` only frees memory if at
    // least one record has been inserted in the in-memory sorter. This will not be the case if
    // we have spilled in the previous step.
    //
    // If we did not spill in the previous step then `growPointerArrayIfNecessary` will be a
    // no-op that does not allocate any memory, and therefore can't cause a spill event.
    //
    // Thus there is no need to call `acquireNewPageIfNecessary` again after this step.
    growPointerArrayIfNecessary();
  }

  @Override
  public long spill(long l, MemoryConsumer memoryConsumer) throws IOException {
    logger.warn("SortBasedPusher not support spill yet");
    return 0;
  }

  private long getMemoryUsage() {
    long totalPageSize = 0;
    for (MemoryBlock page : allocatedPages) {
      totalPageSize += page.size();
    }
    return ((inMemSorter == null) ? 0 : inMemSorter.getMemoryUsage()) + totalPageSize;
  }

  private void updatePeakMemoryUsed() {
    long mem = getMemoryUsage();
    if (mem > peakMemoryUsedBytes) {
      peakMemoryUsedBytes = mem;
    }
  }

  /** Return the peak memory used so far, in bytes. */
  long getPeakMemoryUsedBytes() {
    updatePeakMemoryUsed();
    return peakMemoryUsedBytes;
  }

  private long freeMemory() {
    updatePeakMemoryUsed();
    long memoryFreed = 0;
    for (MemoryBlock block : allocatedPages) {
      memoryFreed += block.size();
      freePage(block);
    }
    allocatedPages.clear();
    currentPage = null;
    pageCursor = 0;
    return memoryFreed;
  }

  public void cleanupResources() {
    long freedBytes = freeMemory();
    if (inMemSorter != null) {
      inMemSorter.freeMemory();
      inMemSorter = null;
    }
    taskContext.taskMetrics().incMemoryBytesSpilled(freedBytes);
  }

  public void close() throws IOException {
    cleanupResources();
    try {
      dataPusher.waitOnTermination();
      sendBufferPool.returnPushTaskQueue(dataPusher.getIdleQueue());
    } catch (InterruptedException e) {
      TaskInterruptedHelper.throwTaskKillException();
    }
  }

  // SPARK-29310 opens it to public in Spark 3.0, it's necessary to keep compatible with Spark 2
  @Override
  public long getUsed() {
    return super.getUsed();
  }
}
