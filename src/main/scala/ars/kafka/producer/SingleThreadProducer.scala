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

import ars.kafka.producer.config.ProducerConfig

/** The single thread blocking Kafka producer.
  *
  * @tparam K the key type
  * @tparam V the value type
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait SingleThreadProducer[K, V] {

  /**
    * Starts the producer.
    *
    *
    * @throws IllegalStateException if producer already started
    */
  def start(): Unit

  /**
    * Stops the producer.
    *
    * @throws IllegalStateException if producer already stopping ([[stop()]] method already
    *                               was called or [[start()]] method was not called yet).
    */
  def stop(): Unit

  /**
    * @return configuration (non-null)
    */
  def config: ProducerConfig



}




//def runProducer(key: String, text: String): Unit = {
//
//  import ars.kafka.producer.config.ProducerConfig
//
//  val props = new Properties()
//  props.put("bootstrap.servers", "localhost:9092")
//  props.put("acks", "all")
//  props.put("retries", 0.toString)
//  props.put("batch.size", 16384.toString)
//  props.put("linger.ms", 1.toString)
//  props.put("buffer.memory", 33554432.toString)
//  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//
//  props.put("enable.idempotence", "true")
//
//
//  val producer = new KafkaProducer[String, String](props)
//  try {
//  for (i <- 0 until 100) {
//  val record = new ProducerRecord("my-topic", s"$key: $i", s"$text: $i")
//  producer.send(record)
//}
//} finally {
//  producer.close()
//}
//}
