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
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.KafkaException

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}

/** Default implementation of single thread producer.
  *
  * @param config the configuration (must be non-null)
  * @param ec the execution context (must be non-null). By default `ExecutionContext.global`
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class DefaultProducer[Key, Value](
    override val config: ProducerConfig
)(implicit ec: ExecutionContext = ExecutionContext.global) extends Producer[Key, Value] {

  requireNotNull(config, "config")

  private val producer = createProducer(config)

  override def createProducer(config: ProducerConfig): KafkaProducer[Key, Value] = {
    requireNotNull(config, "config")
    new KafkaProducer[Key, Value](config.allAsJava)
  }

  override def createRecord(topic: String, key: Option[Key], value: Value): ProducerRecord[Key, Value] = {
    requireNotNull(topic, "topic")
    requireNotNull(key, "key")
    requireNotNull(value, "value")

    key.map(new ProducerRecord(topic, _, value)).getOrElse(new ProducerRecord(topic, value))
  }

  override def close(): Unit = producer.close()

  override def close(duration: Duration): Unit = {
    requireNotNull(duration, "duration")

    producer.close(duration.length, duration.unit)
  }

  override def send(topic: String, value: Value): Future[RecordMetadata] = send(topic, None, value)

  override def send(topic: String, key: Option[Key], value: Value): Future[RecordMetadata] = {
    val record = createRecord(topic, key, value)
    send(record)
  }

  override def send(record: ProducerRecord[Key, Value]): Future[RecordMetadata] = {
    requireNotNull(record, "record")

    val promise = Promise[RecordMetadata]()
    producer.send(record, new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          if (metadata != null) processSuccess(metadata)
          else if (exception != null) processFailure(exception)
          else processIncorrectBehaviour()
        }

        def processSuccess(metadata: RecordMetadata): Unit = {
          logger.debug(s"Record with offset='${metadata.offset()}' has been sent to the server")
          promise.success(metadata)
        }

        def processFailure(exception: Exception): Unit = {
          logger.error("Record has not been sent to the server", exception)
          promise.failure(exception)
          // TODO Add retry
        }

        def processIncorrectBehaviour(): Unit = {
          val message = "Incorrect Kafka API behaviour: both `metadata` and `exception` are `null`"
          logger.error(message)
          promise.failure(new IllegalStateException(message))
        }
      })

    promise.future
  }

  //// TODO Tx start

  private var isTxInit = false


//  def inTransaction(block: => Unit): Unit = {
//    if (!isTxInit) {
//      producer.initTransactions()
//    }
//
//    producer.beginTransaction()
//    try {
//      block()
//      producer.commitTransaction()
//    } catch {
//      case e: KafkaException =>
//        producer.abortTransaction()
//
//    }
//
//
//  }


  /// TODO Tx end

  private def logger = Logger[DefaultProducer[_, _]]
}
