/*
 * Copyright 2018 Arsen Ibragimov (ars)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ars.kafka.consumer.unpacker
import ars.kafka.util.{ByteUtils, SerializationUtils}

import scala.util.Try

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class HeaderBodyDeserializationUnpacker[Header <: AnyRef, Body <: AnyRef](bodyDeserializationPredicate: Header => Boolean)
  extends DeserializationUnpacker[(Header, Body)] with HeaderBodyUnpacker[Bytes, Header, Body] {

  private[this] val HeaderSizeArrayLength = 4
  private[this] val HeaderOffset = HeaderSizeArrayLength

  override def split(from: Bytes): Try[(Bytes, Bytes)] = {
    Try {
      val headerSizeBytes = from.slice(0, HeaderOffset)
      val headerArraySize = ByteUtils.bytes2int(headerSizeBytes)

      val bodyOffset = HeaderSizeArrayLength + headerArraySize
      val headerBytes = from.slice(HeaderOffset, bodyOffset)
      val bodyBytes = from.slice(bodyOffset, from.length)

      (headerBytes, bodyBytes)
    }
  }

  override def unpackHeader(headerBytes: Bytes): Try[Header] = {
    SerializationUtils.deserializeObject[Header](headerBytes)
  }

  override def unpackBody(bodyBytes: Bytes): Try[Body] = {
    SerializationUtils.deserializeObject(bodyBytes)
  }

  override def isUnpackBody(header: Header): Boolean = bodyDeserializationPredicate(header)
}
