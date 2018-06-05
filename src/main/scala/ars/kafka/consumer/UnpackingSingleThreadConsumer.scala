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
trait UnpackingSingleThreadConsumer[SerKey, Key, SerValue, Value] extends SingleThreadConsumer[SerKey, SerValue] {

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
  def processUnpacked(key: Option[Key], value: Value): Boolean
}
