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

package org.apache.celeborn.common.util

import java.io.{File, IOException}
import java.util.concurrent.atomic.AtomicBoolean

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.security.UserGroupInformation

import org.apache.celeborn.common.CelebornConf
import org.apache.celeborn.common.exception.CelebornException
import org.apache.celeborn.common.internal.Logging

object CelebornHadoopUtils extends Logging {
  private var logPrinted = new AtomicBoolean(false)
  private[celeborn] def newConfiguration(conf: CelebornConf): Configuration = {
    val hadoopConf = new Configuration()
    if (conf.hdfsDir.nonEmpty) {
      val path = new Path(conf.hdfsDir)
      val scheme = path.toUri.getScheme
      val disableCacheName = String.format("fs.%s.impl.disable.cache", scheme)
      hadoopConf.set("dfs.replication", "2")
      hadoopConf.set(disableCacheName, "false")
      if (logPrinted.compareAndSet(false, true)) {
        logInfo(
          "Celeborn overrides some HDFS settings defined in Hadoop configuration files, " +
            s"including '$disableCacheName=false' and 'dfs.replication=2'. " +
            "It can be overridden again in Celeborn configuration with the additional " +
            "prefix 'celeborn.hadoop.', e.g. 'celeborn.hadoop.dfs.replication=3'")
      }
    }
    appendSparkHadoopConfigs(conf, hadoopConf)
    hadoopConf
  }

  private def appendSparkHadoopConfigs(conf: CelebornConf, hadoopConf: Configuration): Unit = {
    // Copy any "celeborn.hadoop.foo=bar" celeborn properties into conf as "foo=bar"
    for ((key, value) <- conf.getAll if key.startsWith("celeborn.hadoop.")) {
      hadoopConf.set(key.substring("celeborn.hadoop.".length), value)
    }
  }

  def getHadoopFS(conf: CelebornConf): FileSystem = {
    val hadoopConf = newConfiguration(conf)
    initKerberos(conf, hadoopConf)
    new Path(conf.hdfsDir).getFileSystem(hadoopConf)
  }

  def deleteHDFSPathOrLogError(hadoopFs: FileSystem, path: Path, recursive: Boolean): Unit = {
    try {
      val startTime = System.currentTimeMillis()
      hadoopFs.delete(path, recursive)
      logInfo(
        s"Delete HDFS ${path}(recursive=$recursive) costs " +
          Utils.msDurationToString(System.currentTimeMillis() - startTime))
    } catch {
      case e: IOException =>
        logError(s"Failed to delete HDFS ${path}(recursive=$recursive) due to: ", e)
    }
  }

  def initKerberos(conf: CelebornConf, hadoopConf: Configuration): Unit = {
    UserGroupInformation.setConfiguration(hadoopConf)
    if ("kerberos".equals(hadoopConf.get("hadoop.security.authentication").toLowerCase)) {
      (conf.hdfsStorageKerberosPrincipal, conf.hdfsStorageKerberosKeytab) match {
        case (Some(principal), Some(keytab)) =>
          logInfo(
            s"Attempting to login to Kerberos using principal: $principal and keytab: $keytab")
          if (!new File(keytab).exists()) {
            throw new CelebornException(s"Keytab file: $keytab does not exist")
          }
          UserGroupInformation.loginUserFromKeytab(principal, keytab)
        case _ =>
          logInfo("Kerberos is enabled without principal and keytab supplied," +
            " assuming keytab is managed externally")
          UserGroupInformation.getCurrentUser()
      }
    }
  }
}
