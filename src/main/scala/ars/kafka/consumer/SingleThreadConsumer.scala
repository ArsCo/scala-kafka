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
import ars.kafka.consumer.retry.RetryPolicy
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.concurrent.duration.Duration

/** The single thread blocking Kafka consumer.
  * It abstracts synchronous kafka polling with custom commit implementation.
  *
  * @tparam K the key type
  * @tparam V the value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait SingleThreadConsumer[K, V] {

  // TODO Verify algorithm!!!
  /**
    * Starts the consumer.
    *
    * This method blocks the calling thread and polls kafka server to
    * get messages. To stop consuming you must call [[stop()]] from
    * another thread.
    *
    * The processing template:
    * {{{
    *   def start() {
    *     while (!isStopped) {
    *       val records = pollNextChunk()
    *       if (process(records)) {
    *         commit()
    *       } else {
    *         rollback()
    *       }
    *     }
    *   }
    *
    *   def process(records: ConsumerRecords[K, V]): Boolean = {
    *     for (record <- records) {
    *       if(!process(record)) return false
    *     }
    *   }
    * }}}
    *
    * @throws IllegalStateException if consumer already started
    */
  def start(): Unit

  /**
    * Stops the consumer.
    *
    * @throws IllegalStateException if consumer already stopping ([[stop()]] method already
    *                               was called or [[start()]] method was not called yet).
    */
  def stop(): Unit

  /**
    * Creates new consumer.
    *
    * @param config the consumer configuration (must be non-null)
    *
    * @return the new consumer
    */
  def createConsumer(config: ConsumerConfig): KafkaConsumer[K, V]

  /**
    * Subscribes consumer to topic(s).
    *
    * @param consumer the consumer (must be non-null)
    */
  def subscribe(consumer: KafkaConsumer[K, V]): Unit

  /**
    * Gets the next portion of messages from kafka and process
    *
    * @param consumer the consumer (must be non-null)
    */
  def process(consumer: KafkaConsumer[K, V]): Unit

  /** Processes consumed records. If This method returns `false` then [[process()]]
    * will not be called, otherwise [[process()]] method will be called for each
    * consumed record.
    *
    * @param records the consumed records (must be non-null)
    *
    * @return `true` if records must be processed in [[process()]], and `false` otherwise
    */
  def process(records: ConsumerRecords[K, V]): Boolean // TODO: ProcessCompletionStatus

  /**
    * Processes record. If This method returns `false` then all records from previous call of
    * [[process()]] will not be committed and [[process()]] method will not be called for the
    * next record (if it exists).
    *
    * @param record the record (must be non-null)
    *
    * @return `true` if record was processed successfully, and `false` otherwise.
    */
  def process(record: ConsumerRecord[K, V]): ProcessCompletionStatus

  /** Handles unexpected exceptions.
    *
    * @param exception the handling exception (must be non-null)
    */
  def handleUnexpectedException(exception: Exception): Unit

  /**
    * Closes consumer.
    *
    * @param consumer the consumer (must be non-null)
    */
  def close(consumer: KafkaConsumer[K, V]): Unit

  /**
    * @return configuration (non-null)
    */
  def config: ConsumerConfig

  /**
    * Gets the polling timeout. It can be used to dynamically change polling frequency.
    *
    * @return the timeout duration (non-null)
    */
  def pollTimeout: Duration

  /**
    * Gets underling native [[KafkaConsumer]] instance.
    *
    * @throws IllegalStateException if consumer calling when consumer was not started or have bean stopped
    *
    * @return the consumer (non-null)
    */
  def nativeConsumer: KafkaConsumer[K, V]

  /**
    * Gets retry policy.
    *
    * @return the retry policy (non-null)
    */
  def retryPolicy: RetryPolicy
}

object SingleThreadConsumer {

  /** Default consumer polling timeout. */
  val DefaultPollingTimeout = 500000
}
