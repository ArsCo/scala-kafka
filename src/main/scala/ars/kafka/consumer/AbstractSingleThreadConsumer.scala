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

import ars.kafka.consumer.AbstractSingleThreadConsumer._
import ars.kafka.consumer.config.ConsumerConfig
import ars.kafka.util.KafkaUtils
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.KafkaException

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
    override val config: ConsumerConfig,
    topics: Seq[String],
    timeout: Duration = DefaultPollingTimeout.microsecond
) extends SingleThreadConsumer[K, V] {

  requireNotNull(config, "config")
  requireNotBlank(topics, "topics")
  requireNotNull(timeout, "timeout")

  @volatile
  private var isStop = false

  @volatile
  private var wasStarted = false

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


  /** @inheritdoc */
  override def start(): Unit = {
    if (wasStarted) throw new IllegalStateException("Already was started.")
    wasStarted = true

    val consumer = createConsumer(config)
    try {
      subscribe(consumer, topics)
      while (!isStop) processNext(consumer)

    } catch {
      case e: Exception => handleUnexpectedException(e)

    } finally {
      tryCloseTolerant(consumer)
    }
  }

  /** @inheritdoc */
  override def process(records: ConsumerRecords[K, V]): Boolean = {
    for (record <- records.asScala) {
      try {
        val isSuccess = process(record)
        if (!isSuccess) return false
      } catch {
        case e: Exception =>
          logger.error("The `processor` MUST NOT throw an exception:", e)
          return false
      }
    }
    true
  }

  /** @inheritdoc */
  override def stop(): Unit = {
    if (!wasStarted) throw new IllegalStateException("Not started yet.")
    isStop = true
  }

  /** @inheritdoc */
  override def pollTimeout: Duration = timeout

  private[kafka] def handleUnexpectedException(e: Exception): Unit = {
    logger.error("There's an unexpected exception was thrown.", e)
  }

  private[kafka] def createConsumer(config: ConsumerConfig): KafkaConsumer[K, V] = {
    val consumer = new KafkaConsumer[K, V](KafkaUtils.toProps(config.all))
    logger.info("New consumer instance was created.")
    consumer
  }

  private[kafka] def subscribe(consumer: KafkaConsumer[K, V], topics: Seq[String]): Unit = {
    consumer.subscribe(topics.asJavaCollection)
    logger.info(s"Consumer have subscribed the topics [${topics.mkString(",")}].")
  }

  private[kafka] def processNext(consumer: KafkaConsumer[K, V]): Unit = {
    val records = pollRecords(consumer)
    val isSuccess = process(records)
    if (isSuccess) consumer.commitSync()
  }

  private[kafka] def pollRecords(consumer: KafkaConsumer[K, V]): ConsumerRecords[K, V] = {
    consumer.poll(pollTimeout.toMillis)
  }

  private[kafka] def tryClose(consumer: KafkaConsumer[K, V]): Boolean = {
    try {
      consumer.close()
      true
    } catch {
      case e: KafkaException =>
        logger.error("Error while trying to close consumer.", e)
        false
    }
  }

  private[kafka] def tryCloseTolerant(consumer: KafkaConsumer[K, V]): Unit = {
    !tryClose(consumer) && tryClose(consumer) // Close consumer (try twice)
    logger.error("The consuming was stopped.")
  }

  private def logger = Logger[AbstractSingleThreadConsumer[_, _]]
}

object AbstractSingleThreadConsumer {

  /** Default consumer polling timeout. */
  val DefaultPollingTimeout = 3000
}