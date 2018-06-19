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

package ars.kafka.producer.pack.serialization

import ars.kafka.producer.pack.HeaderBodyPacker
import ars.kafka.util.ByteUtils
import ars.kafka.util.SerializationUtils.serializeValue
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success, Try}

/** Packs pair of header and body to binary array by standard Java serialization.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class HeaderBodySerializationPacker[Header <: AnyRef, Body <: AnyRef]
  extends HeaderBodyPacker[Header, Body, Array[Byte]] {

  /** @inheritdoc */
  override def pack(from: (Header, Body)): Try[Array[Byte]] = {
    requireNotNull(from, "from")
    requireNotNull(from._1, "from._1")
    requireNotNull(from._2, "from._2")

    val (header, body) = from
    pack(header, body)
  }

  /** @inheritdoc */
  override def pack(header: Header, body: Body): Try[Array[Byte]] = {
    (packHeader(header), packBody(body)) match {
      case (Success(h), Success(b)) =>
        Success(h ++ b)

      case (Failure(e), Success(_)) =>
        val message = s"Can't serialize header '$header'"
        logger.error(message, e)
        Failure(new IllegalArgumentException(message, e)) // TODO Custom exception

      case (Success(_), Failure(e)) =>
        val message = s"Can't serialize body '$body'"
        logger.error(message, e)
        Failure(new IllegalArgumentException(message, e)) // TODO Custom exception

      case (Failure(e1), Failure(e2)) =>
        val message1 = s"Can't serialize header '$header'"
        logger.error(message1, e1)

        val message2 = s"Can't serialize body '$body'"
        logger.error(message2, e2)
        Failure(new IllegalArgumentException(message1, e1))
    }
  }

  /** @inheritdoc */
  override def packBody(body: Body): Try[Array[Byte]] = {
    serializeValue(body)
  }

  /** @inheritdoc */
  override def packHeader(header: Header): Try[Array[Byte]] = {
    serializeValue(header).map { headerBytes =>
      val headerSizeBytes = ByteUtils.int2bytes(headerBytes.length)
      headerSizeBytes ++ headerBytes
    }
  }

  private[this] def logger = Logger[HeaderBodySerializationPacker[_, _]]
}
