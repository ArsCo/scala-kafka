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

package ars.kafka.producer.config

import ars.precondition.require.Require.Default._

/** Serializers config part.
  *
  * @param key the key serializer full class name (must be non-blank)
  *            Sets consumer `key.deserializer` parameter.
  * @param value the value serializer full class name (must be non-blank)
  *              Sets consumer `value.deserializer` parameter.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class Serializers(key: String, value: String) {
  requireNotBlank(key, "key")
  requireNotBlank(value, "value")
}
object Serializers {

  /**
    * Byre buffer deserializer full class name.
    */
  final val ByteBufferSerializer = "org.apache.kafka.common.serialization.ByteBufferSerializer"


  final val ByteBufferSerializers = Serializers(ByteBufferSerializer, ByteBufferSerializer)
}