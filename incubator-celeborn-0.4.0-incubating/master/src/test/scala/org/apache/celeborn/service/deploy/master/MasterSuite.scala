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

package org.apache.celeborn.service.deploy.master

import com.google.common.io.Files
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.funsuite.AnyFunSuite

import org.apache.celeborn.common.CelebornConf
import org.apache.celeborn.common.internal.Logging
import org.apache.celeborn.common.util.CelebornExitKind

class MasterSuite extends AnyFunSuite
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with Logging {

  def getTmpDir(): String = {
    val tmpDir = Files.createTempDir()
    tmpDir.deleteOnExit()
    tmpDir.getAbsolutePath
  }

  test("test single node startup functionality") {
    val conf = new CelebornConf()
    conf.set(CelebornConf.HA_ENABLED.key, "false")
    conf.set(CelebornConf.HA_MASTER_RATIS_STORAGE_DIR.key, getTmpDir())
    conf.set(CelebornConf.WORKER_STORAGE_DIRS.key, getTmpDir())
    conf.set(CelebornConf.METRICS_ENABLED.key, "true")
    conf.set(CelebornConf.MASTER_HTTP_HOST.key, "127.0.0.1")
    conf.set(CelebornConf.MASTER_HTTP_PORT.key, "11112")

    val args = Array("-h", "localhost", "-p", "9097")

    val masterArgs = new MasterArguments(args, conf)
    val master = new Master(conf, masterArgs)
    new Thread() {
      override def run(): Unit = {
        master.initialize()
      }
    }.start()
    Thread.sleep(5000L)
    master.stop(CelebornExitKind.EXIT_IMMEDIATELY)
    master.rpcEnv.shutdown()
  }
}
