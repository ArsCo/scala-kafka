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

import ars.kafka.config.ProducerConfig
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import ars.precondition.require.Require.Default._

/** Producer that packs key and value before sending.
  *
  * @param config the configuration (must be non-null)
  * @param keyPacker the key packer (must be non-null)
  * @param valuePacker the value packer (must be non-null)
  *
  * @tparam Key the key type
  * @tparam SerKey the serialized key type
  * @tparam Value the value type
  * @tparam SerValue the serialized value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class AbstractPackingProducer[Key, SerKey, Value, SerValue](
    config: ProducerConfig,
    override val keyPacker: Packer[Key, SerKey],
    override val valuePacker: Packer[Value, SerValue]
) extends DefaultProducer[SerKey, SerValue](config) with PackingProducer[Key, SerKey, Value, SerValue] {

  requireNotNull(keyPacker, "keyPacker")
  requireNotNull(valuePacker, "valuePacker")


  /** @inheritdoc */
  override def createRecord(topic: String, key: Option[SerKey], value: SerValue): ProducerRecord[SerKey, SerValue] = {

    super.createRecord(topic, key, value)
  }

  override def sendPacked(topic: String, key: Option[Key], value: Value): Future[RecordMetadata] = { // TODO To trait
    requireNotNull(key, "key")
    requireAllNotNull(key, "key")

    key.map(sendKeyValue(topic, _, value)).getOrElse(sendValue(topic, value))
  }

  override def sendPacked(topic: String, value: Value): Future[RecordMetadata] = {
    sendPacked(topic, None, value)
  }

  private def sendKeyValue(topic: String, sourceKey: Key, value: Value) = {
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

  private def sendValue(topic: String, value: Value) = {
    packValue(value) match {
      case Success(v) =>
        logger.trace(s"Was packed: (None, $value) => (null, $v).")
        send(topic, v)
      case Failure(e) =>
        logger.error(s"Value packing was failed for '$value'", e)
        Future.failed(e)
    }
  }

  private def packKey(key: Key) = keyPacker.pack(key)
  private def packValue(value: Value) = valuePacker.pack(value)

  private[this] def logger = Logger[AbstractPackingProducer[_, _, _, _]]
}

