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
import ars.kafka.consumer.unpacker.Unpacker

import scala.concurrent.duration.{Duration, _}

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
abstract class AbstractUnpackingConsumer[SerKey, Key, SerValue, Value](
    config: ConsumerConfig,
    topics: Seq[String],
    override val keyUnpacker: Unpacker[SerKey, Key],
    override val valueUnpacker: Unpacker[SerValue, Value],
    timeout: Duration = DefaultPollingTimeout.microsecond
) extends AbstractSingleThreadConsumer[SerKey, SerValue](config, topics, timeout)
  with UnpackingConsumer[SerKey, Key, SerValue, Value]



