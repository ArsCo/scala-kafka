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
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import ars.precondition.require.Require.Default._

import scala.concurrent.Future


/** The single thread blocking Kafka producer.
  *
  * @tparam K the key type
  * @tparam V the value type
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait Producer[K, V] extends AutoCloseable {

  /**
    * Gets the configuration.
    *
    * @return the configuration (non-null)
    */
  def config: ProducerConfig

  /**
    * Creates new producer instance.
    *
    * @param config the producer configuration (must be non-null)
    *
    * @return the new producer (non-null)
    */
  def createProducer(config: ProducerConfig): KafkaProducer[K, V]

  /**
    * Creates new record.
    *
    * @param topic the topic (must be non-blank)
    * @param key the key (must be non-null)
    * @param value the value (must be non-null)
    *
    * @return the new producer record (non-null)
    */
  def createRecord(topic: String, key: Option[K], value: V): ProducerRecord[K, V]

  /**
    * Sends the message to the topic.
    *
    * @param topic the topic (must be non-blank)
    * @param key the key (must be non-null)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def send(topic: String, key: Option[K], value: V): Future[RecordMetadata]

  /**
    * Sends the message without key to the topic.
    *
    * @param topic the topic (must be non-blank)
    * @param value the value (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def send(topic: String, value: V): Future[RecordMetadata]

  /**
    * Sends the record to the topic.
    *
    * @param record the record (must be non-null)
    *
    * @return the future of result metadata (non-null)
    */
  def send(record: ProducerRecord[K, V]): Future[RecordMetadata]


  /**
    * Closes the producer.
    */
  override def close(): Unit
}

object Producer {

  /**
    * Creates new producer by `creator` with configuration `config`.
    *
    * @param config the configuration (must be non-null)
    * @param creator the creator (must be non-null)
    * @param block the code block (must be non-null)
    */
  def withProducer[K, V](
      config: ProducerConfig,
      creator: ProducerConfig => Producer[K,V] = (c: ProducerConfig) => new DefaultProducer[K,V](c)
  )(block: => Producer[K, V] => Unit): Unit = {

    requireNotNull(config, "config")
    requireNotNull(creator, "creator")
    requireNotNull(block , "block")

    val producer = creator(config)
    try {
      block(producer)
    } catch {
      case e: Exception =>
        logger.error("Unexpected exception during execution. Producer will be closed carefully.", e)
    } finally {
      producer.close()
    }
  }

  private def logger = Logger[Producer.type]
}




