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

import ars.kafka.util.SerializationUtils.serializeValue

import scala.util.Try

/** Packs value to binary array by standard Java serialization.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class SimpleSerializationPacker[From <: AnyRef] extends SerializationPacker[From] {

  /**
    * Packs the `from` value to standard Java serialization form.
    *
    * @param from the from value (must be non-null).
    *
    * @return the result (non-null)
    */
  override def pack(from: From): Try[Array[Byte]] = serializeValue(from)
}
