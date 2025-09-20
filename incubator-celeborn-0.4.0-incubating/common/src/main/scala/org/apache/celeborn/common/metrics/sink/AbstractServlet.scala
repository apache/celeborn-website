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
package org.apache.celeborn.common.metrics.sink

import org.apache.celeborn.common.CelebornConf
import org.apache.celeborn.common.internal.Logging
import org.apache.celeborn.common.metrics.source.Source

abstract class AbstractServlet(sources: Seq[Source]) extends Sink with Logging {
  def getHandlers(conf: CelebornConf): Array[ServletHttpRequestHandler] = {
    Array[ServletHttpRequestHandler](
      createHttpRequestHandler())
  }

  def createHttpRequestHandler(): ServletHttpRequestHandler

  def getMetricsSnapshot: String = {
    sources.map(_.getMetrics).mkString
  }

  override def start(): Unit = {}

  override def stop(): Unit = {}

  override def report(): Unit = {}
}

abstract class ServletHttpRequestHandler(path: String) extends Logging {

  def handleRequest(uri: String): String

  def getServletPath(): String = path

}
