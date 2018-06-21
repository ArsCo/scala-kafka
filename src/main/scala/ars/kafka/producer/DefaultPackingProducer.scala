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
import ars.kafka.producer.packer.Packer
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
class DefaultPackingProducer[Key, SerKey, Value, SerValue](
    config: ProducerConfig,
    override val keyPacker: Packer[Key, SerKey],
    override val valuePacker: Packer[Value, SerValue]
) extends DefaultProducer[SerKey, SerValue](config) with PackingProducer[Key, SerKey, Value, SerValue] {

  requireNotNull(keyPacker, "keyPacker")
  requireNotNull(valuePacker, "valuePacker")
}

