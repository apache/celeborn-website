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

package org.apache.celeborn.common.haclient;

import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

// This class is reserved for compatible with 0.2 client
public class MasterNotLeaderException extends IOException {

  private static final long serialVersionUID = -6230015203034327259L;

  private final String leaderPeer;

  public static final String LEADER_NOT_PRESENTED = "leader is not present";

  public MasterNotLeaderException(
      String currentPeer, String suggestedLeaderPeer, @Nullable Throwable cause) {
    super(
        String.format(
            "Master:%s is not the leader. Suggested leader is Master:%s.%s",
            currentPeer,
            suggestedLeaderPeer,
            cause == null
                ? StringUtils.EMPTY
                : String.format(" Exception:%s.", cause.getMessage())),
        cause);
    this.leaderPeer = suggestedLeaderPeer;
  }

  public String getSuggestedLeaderAddress() {
    return leaderPeer;
  }
}
