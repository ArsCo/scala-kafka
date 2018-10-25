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
import ars.kafka.consumer.ProcessCompletionStatuses.{Retry, Skip, Success}
import ars.kafka.consumer.SingleThreadConsumer.DefaultPollingTimeout
import ars.kafka.util.KafkaUtils
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.{KafkaException, TopicPartition}

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.control.NonFatal

/** Abstract common consumer.
  *
  * @param config the consumer configuration (must be non-null)
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class AbstractCommonSingleThreadConsumer[K, V](
    override val config: ConsumerConfig
) extends SingleThreadConsumer[K, V] { // TODO Change to Consumer[K,V]

  requireNotNull(config, "config")

  @volatile
  private var isStop = false

  @volatile
  private var wasStarted = false

  @volatile
  private var consumer: KafkaConsumer[K, V] = _

  override def start(): Unit = { // TODO Add start/stop interceptors
    if (wasStarted) throw new IllegalStateException("Already was started.")
    if (isStop) throw new IllegalStateException("Already was stopped.")

    wasStarted = true

    consumer = createConsumer(config)

    startConsumingLoop()
  }

  protected def startConsumingLoop(): Unit = {
    try {
      subscribe(consumer)
      while (!isStop) process(consumer)

    } catch {
      case e: Exception => handleUnexpectedException(e)

    } finally {
      isStop = true
      wasStarted = false
      close(consumer)
    }
  }

  override def createConsumer(config: ConsumerConfig): KafkaConsumer[K, V] = {
    val consumer = new KafkaConsumer[K, V](KafkaUtils.toProps(config.all))
    logger.info("New consumer instance was created.")
    consumer
  }

  /** @inheritdoc */
  override def stop(): Unit = {
    if (!wasStarted) throw new IllegalStateException("Not started yet.")
    isStop = true
  }

  /** @inheritdoc */
  override def handleUnexpectedException(e: Exception): Unit = {
    logger.error("There's an unexpected exception was thrown.", e)
  }

  /** @inheritdoc */
  override def process(records: ConsumerRecords[K, V]): Boolean = { // TODO: ProcessCompletionStatus
    for (record <- records.asScala) {
      try {
        process(record) match {
          case Retry => return false
          case Skip => return false
          case Success => // do nothing
        }
//        if (!completionStatus) return false
      } catch {
        case e: Exception =>
          logger.error("The `processor` MUST NOT throw an exception:", e)
          return false
      }
    }
    true
  }

  /** @inheritdoc */
  override def pollTimeout: Duration = DefaultPollingTimeout.second

  /** @inheritdoc */
  override def close(consumer: KafkaConsumer[K, V]): Unit = {
    !tryClose(consumer) && tryClose(consumer) // Close consumer (try twice if need)
    logger.error("The consuming was stopped.")
  }

  /** @inheritdoc */
  override def process(consumer: KafkaConsumer[K, V]): Unit = {
    try {
      val records = pollRecords(consumer)
      val isSuccess = process(records)
      if (isSuccess) consumer.commitSync()
    } catch {
      case NonFatal(e) =>
        logger.error("Error:", e)
    }
  }


  private[this] def commitAll(records: ConsumerRecords[K, V]): Unit = { // TODO: Change to use retry policy
    val maxOffset = records.asScala.map(_.offset()).max
    commitInfo(maxOffset)
  }


  private[this] def firstAssignedTopic(): TopicPartition = {
    val iterator = consumer.assignment().iterator()
    if (iterator.hasNext) iterator.next()
    else throw new IllegalStateException("No assigned partitions.")
  }

  private[this] def commitInfo(offset: Long): Map[TopicPartition, OffsetAndMetadata] = {
    Map(
      firstAssignedTopic() -> new OffsetAndMetadata(offset)
    )
  }

  /** @inheritdoc */
  override def nativeConsumer: KafkaConsumer[K, V] = {
    if (!wasStarted) throw new IllegalStateException("Not started yet.")
    if (isStop) throw new IllegalStateException("Already was stopped.")

    consumer
  }

  private[kafka] def pollRecords(consumer: KafkaConsumer[K, V]): ConsumerRecords[K, V] = {
    consumer.poll(pollTimeout.toMillis)
  }

  protected def tryClose(consumer: KafkaConsumer[K, V]): Boolean = {
    try {
      consumer.close()
      true
    } catch {
      case e: KafkaException =>
        logger.error("Error while trying to close consumer.", e)
        false
    }
  }

  private def logger = Logger[AbstractCommonSingleThreadConsumer[_, _]]
}
