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
import ars.kafka.consumer.unpacker.{DeserializationUnpacker, SimpleDeserializationUnpacker}

import scala.concurrent.duration.{Duration, _}

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class AbstractDeserializationUnpackingConsumer[Key, Value](
    config: ConsumerConfig,
    topics: Seq[String],
    keyUnpacker: DeserializationUnpacker[Key] = new SimpleDeserializationUnpacker[Key],
    valueUnpacker: DeserializationUnpacker[Value] = new SimpleDeserializationUnpacker[Value],
    timeout: Duration = DefaultPollingTimeout.microsecond
) extends AbstractUnpackingConsumer[Array[Byte], Key, Array[Byte], Value](config, topics, keyUnpacker, valueUnpacker, timeout)
