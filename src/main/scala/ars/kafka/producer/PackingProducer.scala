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

import org.apache.kafka.clients.producer.RecordMetadata

import scala.concurrent.Future

/** Packing producer.
  *
  * @tparam Key the key type before packing
  * @tparam Value the value type before packing
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait PackingProducer[Key, SerKey, Value, SerValue] extends Producer[SerKey, SerValue]{

  /** Packs `key` with `keyPacker`, packs `value` with `valuePacker` and sends the record.
    *
    * @param topic the topic (must be non-blank)
    * @param key the key (must be non-null)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def sendPacked(topic: String, key: Option[Key], value: Value): Future[RecordMetadata]

  /** Packs `value` with `valuePacker` and sends the record.
    *
    * @param topic the topic (must be non-blank)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def sendPacked(topic: String, value: Value): Future[RecordMetadata]

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
}
