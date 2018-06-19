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

package ars.kafka.consumer.unpack
import ars.kafka.util.{ByteUtils, SerializationUtils}

import scala.util.{Failure, Success, Try}

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class HeaderedDeserializationUnpacker[Header <: AnyRef, Body <: AnyRef]
(bodyDeserializationPredicate: Header => Boolean)
  extends DeserializationUnpacker[(Header, Body)] {

  /**
    * Unpacks the standard Java serialized value (byte array) to pair of ([[(Header, Body)]], ).
    *
    * @param from the from value (must be non-null).d
    * @return the result (non-null)
    */
  override def unpack(from: Array[Byte]): Try[(Header, Body)] = {

    deserializeHeader(from).flatMap { case (header, bodyBytes) =>
        if (isDeserializeBody(header)) deserialiazeBody(bodyBytes).map(header -> _)
        else Failure(new IllegalStateException("Can't deserialize body."))
    }
  }

  def isDeserializeBody(header: Header): Boolean = bodyDeserializationPredicate(header)

  private[this] val HeaderSizeArrayLength = 4
  private[this] val HeaderOffset = HeaderSizeArrayLength


  private[this] def deserializeHeader(from: Array[Byte]): Try[(Header, Array[Byte])] = {

    val headerSizeBytes = from.slice(0, HeaderOffset)
    val headerArraySize = ByteUtils.bytes2int(headerSizeBytes)

    val bodyOffset = HeaderSizeArrayLength + headerArraySize

    val headerBytes = from.slice(HeaderOffset, bodyOffset)
    val header = SerializationUtils.deserializeObject[Header](headerBytes)

    header.map { h =>
      val bodyBytes = from.slice(bodyOffset, from.length)
      (h, bodyBytes)
    }
  }

  def deserialiazeBody(from: Array[Byte]): Try[Body] = {
    SerializationUtils.deserializeObject(from)
  }


  def processHeader(header: Header): Boolean = {
    true // TODO
  }
}
