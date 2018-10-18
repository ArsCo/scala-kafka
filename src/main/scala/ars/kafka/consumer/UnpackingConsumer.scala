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

import ars.kafka.consumer.unpacker.Unpacker
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.util.{Failure, Success}

/** Unpacking single thread consumer.
  *
  * @tparam Key the key type
  * @tparam SerKey the serialized key type
  * @tparam Value the value type
  * @tparam SerValue the serialized value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait UnpackingConsumer[SerKey, Key, SerValue, Value] extends SingleThreadConsumer[SerKey, SerValue] { // TODO Change to Consumer[K,V] for other consumers

  /** Key unpacker.
    *
    * @return the key unpacker (non-null)
    */
  def keyUnpacker: Unpacker[SerKey, Key]

  /** Value unpacker.
    *
    * @return the value unpacker (non-null)
    */
  def valueUnpacker: Unpacker[SerValue, Value]

  /**
    * Processes unpacked `key` and `value`.
    *
    * @param key the key (must be non-null)
    * @param value the value (must be non-null)
    *
    * @return `true` if records was processed successfully, and `false` otherwise
    */
  def processUnpacked(key: Option[Key], value: Value): ProcessCompletionStatus


  override def process(record: ConsumerRecord[SerKey, SerValue]): ProcessCompletionStatus = {
    val serValue = record.value()
    Option(record.key())
      .map(processKeyValue(_, serValue))
      .getOrElse(processValue(serValue))
  }

  private def processKeyValue(serKey: SerKey, serValue: SerValue): ProcessCompletionStatus = {
    (unpackKey(serKey), unpackValue(serValue)) match {
      case (Success(k), Success(v)) =>
        logger.debug(s"Was unpacked: ($serKey, $serValue) => ($k, $v).")
        processUnpacked(Some(k), v)

      case (Failure(e), Success(_)) =>
        logger.error(s"Key unpacking was failed for '$serKey'", e)
        ProcessCompletionStatuses.Skip // TODO

      case (Success(_), Failure(e)) =>
        logger.error(s"Value unpacking was failed for '$serValue'", e)
        ProcessCompletionStatuses.Skip // TODO

      case (Failure(ke), Failure(ve)) =>
        logger.error(s"Key packing was failed for '$serKey'", ke)
        logger.error(s"Value packing was failed for '$serValue'", ve)
        ProcessCompletionStatuses.Skip // TODO

      case _ =>
        logger.error(s"Unexpected behaviour: key='$serKey', value='$serValue'")
        ProcessCompletionStatuses.Skip // TODO
    }
  }

  private def processValue(serValue: SerValue): ProcessCompletionStatus = {
    unpackValue(serValue) match {
      case Success(v) =>
        logger.debug(s"Value was unpacked: '$serValue' => '$v'.")
        processUnpacked(None, v)
      case Failure(e) =>
        logger.error(s"Value unpacking was failed for '$serValue'", e)
        ProcessCompletionStatuses.Skip // TODO
    }
  }

  private def unpackKey(key: SerKey) = keyUnpacker.unpack(key)
  private def unpackValue(value: SerValue) = valueUnpacker.unpack(value)

  private def logger = Logger[UnpackingConsumer[_, _, _, _]]
}
