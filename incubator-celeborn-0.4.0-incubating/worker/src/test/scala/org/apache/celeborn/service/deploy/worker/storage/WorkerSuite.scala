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

package org.apache.celeborn.service.deploy.worker.storage

import java.io.File
import java.util
import java.util.{HashSet => JHashSet}

import scala.collection.JavaConverters._

import org.junit.Assert
import org.mockito.MockitoSugar._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import org.apache.celeborn.common.CelebornConf
import org.apache.celeborn.common.identity.UserIdentifier
import org.apache.celeborn.common.protocol.{PartitionLocation, PartitionSplitMode, PartitionType}
import org.apache.celeborn.common.util.{CelebornExitKind, JavaUtils, ThreadUtils}
import org.apache.celeborn.service.deploy.worker.{Worker, WorkerArguments}

class WorkerSuite extends AnyFunSuite with BeforeAndAfterEach {
  private var worker: Worker = _
  private val conf = new CelebornConf()
  private val workerArgs = new WorkerArguments(Array(), conf)

  override def beforeEach(): Unit = {
    assert(null == worker)
  }

  override def afterEach(): Unit = {
    if (null != worker) {
      worker.rpcEnv.shutdown()
      worker.stop(CelebornExitKind.EXIT_IMMEDIATELY)
      worker = null
    }
  }

  test("clean up") {
    conf.set(CelebornConf.WORKER_STORAGE_DIRS.key, "/tmp")
    worker = new Worker(conf, workerArgs)

    val pl1 = new PartitionLocation(0, 0, "12", 0, 0, 0, 0, PartitionLocation.Mode.PRIMARY)
    val pl2 = new PartitionLocation(1, 0, "12", 0, 0, 0, 0, PartitionLocation.Mode.REPLICA)

    worker.storageManager.createWriter(
      "1",
      1,
      pl1,
      100000,
      PartitionSplitMode.SOFT,
      PartitionType.REDUCE,
      true,
      new UserIdentifier("1", "2"))
    worker.storageManager.createWriter(
      "2",
      2,
      pl2,
      100000,
      PartitionSplitMode.SOFT,
      PartitionType.REDUCE,
      true,
      new UserIdentifier("1", "2"))

    Assert.assertEquals(1, worker.storageManager.workingDirWriters.values().size())
    val expiredShuffleKeys = new JHashSet[String]()
    val shuffleKey1 = "1-1"
    val shuffleKey2 = "2-2"
    expiredShuffleKeys.add(shuffleKey1)
    expiredShuffleKeys.add(shuffleKey2)
    worker.cleanup(
      expiredShuffleKeys,
      ThreadUtils.newDaemonCachedThreadPool(
        "worker-clean-expired-shuffle-keys",
        conf.workerCleanThreads))
    Thread.sleep(3000)
    worker.storageManager.workingDirWriters.values().asScala.map(t => assert(t.size() == 0))
  }

  test("flush filewriters") {
    conf.set(CelebornConf.WORKER_STORAGE_DIRS.key, "/tmp")
    worker = new Worker(conf, workerArgs)
    val dir = new File("/tmp")
    val allWriters = new util.HashSet[FileWriter]()
    val map = JavaUtils.newConcurrentHashMap[String, FileWriter]()
    worker.storageManager.workingDirWriters.put(dir, map)
    worker.storageManager.workingDirWriters.asScala.foreach { case (_, writers) =>
      writers.synchronized {
        // Filter out FileWriter that already has IOException to avoid printing too many error logs
        allWriters.addAll(writers.values().asScala.filter(_.getException == null).asJavaCollection)
      }
    }
    Assert.assertEquals(0, allWriters.size())

    val fileWriter = mock[FileWriter]
    when(fileWriter.getException).thenReturn(null)
    map.put("1", fileWriter)
    worker.storageManager.workingDirWriters.asScala.foreach { case (_, writers) =>
      writers.synchronized {
        // Filter out FileWriter that already has IOException to avoid printing too many error logs
        allWriters.addAll(writers.values().asScala.filter(_.getException == null).asJavaCollection)
      }
    }
    Assert.assertEquals(1, allWriters.size())
  }
}
