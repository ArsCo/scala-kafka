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

package ars.kafka.consumer

import ars.kafka.config.ConsumerConfig
import ars.kafka.consumer.SingleThreadConsumer.DefaultPollingTimeout
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.duration.{Duration, _}
import scala.util.{Failure, Success}

/** Consumer that unpacks key and value before processing.
  *
  * @param config the configuration (must be non-null)
  * @param topics the sequence of topic names (must be non-blank)
  * @param keyUnpacker the key unpacker (must be non-null)
  * @param valueUnpacker the value unpacker (must be non-null)
  * @param timeout the initial polling timeout (must be positive)
  *
  * @tparam Key the key type
  * @tparam SerKey the serialized key type
  * @tparam Value the value type
  * @tparam SerValue the serialized value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class AbstractUnpackingSingleThreadConsumer[SerKey, Key, SerValue, Value](
    config: ConsumerConfig,
    topics: Seq[String],
    override val keyUnpacker: Unpacker[SerKey, Key],
    override val valueUnpacker: Unpacker[SerValue, Value],
    timeout: Duration = DefaultPollingTimeout.microsecond
) extends AbstractSingleThreadConsumer[SerKey, SerValue](config, topics, timeout)
  with UnpackingSingleThreadConsumer[SerKey, Key, SerValue, Value] {

  /** @inheritdoc*/
  override def process(record: ConsumerRecord[SerKey, SerValue]): Boolean = {
    val serValue = record.value()
    Option(record.key())
      .map(processKeyValue(_, serValue))
      .getOrElse(processValue(serValue))
  }

  private def processKeyValue(serKey: SerKey, serValue: SerValue) = {
    (unpackKey(serKey), unpackValue(serValue)) match {
      case (Success(k), Success(v)) =>
        logger.debug(s"Was unpacked: ($serKey, $serValue) => ($k, $v).")
        processUnpacked(Some(k), v)

      case (Failure(e), Success(_)) =>
        logger.error(s"Key unpacking was failed for '$serKey'", e)
        false

      case (Success(_), Failure(e)) =>
        logger.error(s"Value unpacking was failed for '$serValue'", e)
        false

      case (Failure(ke), Failure(ve)) =>
        logger.error(s"Key packing was failed for '$serKey'", ke)
        logger.error(s"Value packing was failed for '$serValue'", ve)
        false

      case _ =>
        logger.error(s"Unexpected behaviour: key='$serKey', value='$serValue'")
        false
    }
  }

 override def processUnpacked(key: Option[Key], value: Value): Boolean

  private def processValue(serValue: SerValue) = {
    unpackValue(serValue) match {
      case Success(v) => processUnpacked(None, v)
      case Failure(e) =>
        logger.error(s"Value unpacking was failed for '$serValue'", e)
        false
    }
  }

  private def unpackKey(key: SerKey) = keyUnpacker.unpack(key)
  private def unpackValue(value: SerValue) = valueUnpacker.unpack(value)



  private def logger = Logger[AbstractUnpackingSingleThreadConsumer[_, _, _, _]]
}
