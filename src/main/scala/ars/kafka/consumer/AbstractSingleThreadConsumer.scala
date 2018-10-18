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
import ars.kafka.consumer.SingleThreadConsumer._
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._
import scala.concurrent.duration._


/** Abstract single thread Kafka consumer implementation.
  *
  * @param config the configuration (must be non-null)
  * @param topics the sequence of topic names (must be non-blank)
  * @param timeout the initial polling timeout (must be positive)
  *
  * @tparam K the key type
  * @tparam V the value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class AbstractSingleThreadConsumer[K, V](
    config: ConsumerConfig,
    topics: Seq[String],
    timeout: Duration = DefaultPollingTimeout.microsecond
) extends AbstractCommonSingleThreadConsumer[K, V](config) {

  requireNotNull(config, "config")
  requireNotBlank(topics, "topics")
  requireNotNull(timeout, "timeout")

  /**
    * @param config the configuration (must be non-null)
    * @param topic the topic name (must be non-blank)
    * @param timeout the initial polling timeout (must be positive)
    */
  def this(config: ConsumerConfig, topic: String, timeout: Duration) = this(config, Seq(topic), timeout)

  /**
    * @param config the configuration (must be non-null)
    * @param topic the topic name (must be non-blank)
    */
  def this(config: ConsumerConfig, topic: String) = this(config, Seq(topic),  DefaultPollingTimeout.microsecond)

  override def subscribe(consumer: KafkaConsumer[K, V]): Unit = {
    requireNotNull(consumer, "consumer")

    subscribe(consumer, topics)
  }

  override def pollTimeout: Duration = timeout

  private[kafka] def subscribe(consumer: KafkaConsumer[K, V], topics: Seq[String]): Unit = {
    requireNotNull(consumer, "consumer")
    requireNotBlank(topics, "topics")
    requireAllNotBlank(topics, "topics")

    consumer.subscribe(topics.asJavaCollection)
    logger.info(s"Consumer have subscribed the topics [${topics.mkString(",")}].")
  }

  private[this] final val logger = Logger[AbstractSingleThreadConsumer[_, _]]
}
