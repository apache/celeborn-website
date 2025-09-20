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

package org.apache.celeborn.common.network.protocol;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;

import org.apache.celeborn.common.protocol.PbTransportableError;
import org.apache.celeborn.common.util.ExceptionUtils;

public class TransportableError extends RequestMessage {
  private long streamId;
  private byte[] errorMessage;

  public TransportableError(long streamId, Throwable throwable) {
    this.streamId = streamId;
    this.errorMessage =
        ExceptionUtils.stringifyException(throwable).getBytes(StandardCharsets.UTF_8);
  }

  public TransportableError(long streamId, byte[] errorMessage) {
    this.streamId = streamId;
    this.errorMessage = errorMessage;
  }

  @Override
  public int encodedLength() {
    return 8 + 4 + errorMessage.length;
  }

  @Override
  public void encode(ByteBuf buf) {
    buf.writeLong(streamId);
    buf.writeInt(errorMessage.length);
    buf.writeBytes(errorMessage);
  }

  @Override
  public Type type() {
    return Type.TRANSPORTABLE_ERROR;
  }

  public static TransportableError decode(ByteBuf buf) {
    long streamId = buf.readLong();
    int msgLen = buf.readInt();
    byte[] errorMsg = new byte[msgLen];
    buf.readBytes(errorMsg);
    return new TransportableError(streamId, errorMsg);
  }

  public long getStreamId() {
    return streamId;
  }

  public String getErrorMessage() {
    return new String(errorMessage, StandardCharsets.UTF_8);
  }

  public static TransportableError fromProto(PbTransportableError pb) {
    return new TransportableError(
        pb.getStreamId(), pb.getMessage().getBytes(StandardCharsets.UTF_8));
  }
}
