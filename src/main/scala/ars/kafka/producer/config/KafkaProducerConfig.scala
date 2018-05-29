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

import java.util.Properties

import ars.kafka.util.KafkaUtils


//abstract case class Ack(value: String)
//
//object AckTypes {
//  case object All extends Ack("all")
//
//}
//
//case class KafkaProducerConfig(
//    bootstrapServers: String,
//    bufferMemory: Long = 100*1024*1024,
//    compressionType: String = "gzip",
//    retries: Int = 2,
//    maxInFlightRequests: Int = 1,
//    batchSize: Int = 16384,
//    lingerMs: Int = 1,
//    ack: Ack = AckTypes.All,
//    other: Map[String, Any]
//) {
//
//
//  def toMap: Map[String, Any] = {
//    Map(
//      "bootstrap.servers" -> bootstrapServers,
//
//      "acks" -> "all",
//
//      "buffer.memory" -> bufferMemory,
//      "compression.type" -> compressionType,
//      "retries" -> retries,
//      "max.in.flight.requests.per.connection" -> maxInFlightRequests,
//
//      "batch.size" -> lingerMs,
//      "linger.ms" -> 1,
//
//      "key.serializer" -> "org.apache.kafka.common.serialization.ByteSerializer",
//      "value.serializer" -> "org.apache.kafka.common.serialization.ByteSerializer"
//
//    ) ++ other
//  }
//
//  def toProps: Properties = KafkaUtils.toProps(toMap)
//
//}
