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
import ars.kafka.producer.packer.{SerializationPacker, SimpleSerializationPacker}

/** Serialization packing producer. Serializes key an value to byte arrays
  * with `keyPacker` and `valuePacker` respectively.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class SerializationPackingProducer[Key <: AnyRef, Value <: AnyRef](
    config: ProducerConfig,
    override val keyPacker: SerializationPacker[Key] = new SimpleSerializationPacker[Key],
    override val valuePacker: SerializationPacker[Value] = new SimpleSerializationPacker[Value]
) extends DefaultPackingProducer[Key, Array[Byte], Value, Array[Byte]](config, keyPacker, valuePacker)
