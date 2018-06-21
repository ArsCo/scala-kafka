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

package ars.kafka.producer

import ars.kafka.producer.packer.Packer
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.RecordMetadata

import scala.concurrent.Future
import scala.util.{Failure, Success}

/** Packing producer.
  *
  * @tparam Key the key type before packing
  * @tparam Value the value type before packing
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait PackingProducer[Key, SerKey, Value, SerValue] extends Producer[SerKey, SerValue] {

  /** Packs `key` with `keyPacker`, packs `value` with `valuePacker` and sends the record.
    *
    * @param topic the topic (must be non-blank)
    * @param key the key (must be non-null)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def sendPacked(topic: String, key: Option[Key], value: Value): Future[RecordMetadata] = {
    requireNotNull(key, "key")
    requireAllNotNull(key, "key")

    key.map(sendKeyValue(topic, _, value)).getOrElse(sendValue(topic, value))
  }

  /** Packs `value` with `valuePacker` and sends the record.
    *
    * @param topic the topic (must be non-blank)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def sendPacked(topic: String, value: Value): Future[RecordMetadata] = sendPacked(topic, None, value)

  /** Key packer.
    *
    * @return the key packer (non-null)
    */
  def keyPacker: Packer[Key, SerKey]

  /** Value packer.
    *
    * @return the value packer (non-null)
    */
  def valuePacker: Packer[Value, SerValue]

  private[this] def sendKeyValue(topic: String, sourceKey: Key, value: Value) = {
    (packKey(sourceKey), packValue(value)) match {
      case (Success(k), Success(v)) =>
        logger.trace(s"Was packed: ($sourceKey, $value) => ($k, $v).")
        send(topic, Some(k), v)

      case (Failure(e), Success(_)) =>
        logger.error(s"Key packing was failed for '$sourceKey'", e)
        Future.failed(e)

      case (Success(_), Failure(e)) =>
        logger.error(s"Value packing was failed for '$value'", e)
        Future.failed(e)

      case (Failure(ke), Failure(ve)) =>
        logger.error(s"Key packing was failed for '$sourceKey'", ke)
        logger.error(s"Value packing was failed for '$value'", ve)
        Future.failed(ke) // One of

      case _ =>
        val exception = new IllegalStateException(s"Unexpected behaviour: key='$sourceKey', value='$value'")
        Future.failed(exception)
    }
  }

  private[this] def sendValue(topic: String, value: Value) = {
    packValue(value) match {
      case Success(v) =>
        logger.trace(s"Was packed: (None, $value) => (null, $v).")
        send(topic, v)
      case Failure(e) =>
        logger.error(s"Value packing was failed for '$value'", e)
        Future.failed(e)
    }
  }

  private[this] def packKey(key: Key) = keyPacker.pack(key)
  private[this] def packValue(value: Value) = valuePacker.pack(value)

  private[this] def logger = Logger[PackingProducer[_, _, _, _]]
}
