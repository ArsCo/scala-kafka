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

package ars.kafka.config

import ars.precondition.require.Require.Default._

/** Deserializers config part.
  *
  * @param key the key deserializer full class name (must be non-blank)
  *            Sets consumer `key.deserializer` parameter.
  * @param value the value deserializer full class name (must be non-blank)
  *              Sets consumer `value.deserializer` parameter.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class Deserializers(key: String, value: String) {
  requireNotBlank(key, "key")
  requireNotBlank(value, "value")
}
object Deserializers {

  /** Byte buffer deserializer full class name. */
  final val ByteBufferDeserializer = "org.apache.kafka.common.serialization.ByteBufferDeserializer"

  /** Byte array deserializer full class name. */
  final val ByteArrayDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer"

  /** Byte buffer [[Deserializers]] for key and value. */
  final val ByteBufferDeserializers = Deserializers(ByteBufferDeserializer, ByteBufferDeserializer)

  /** Byte array [[Deserializers]] for key and value. */
  final val ByteArrayDeserializers = Deserializers(ByteArrayDeserializer, ByteArrayDeserializer)
}