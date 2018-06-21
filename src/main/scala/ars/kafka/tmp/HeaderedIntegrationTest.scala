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

package ars.kafka.tmp

import java.io.Serializable
import java.time.Instant
import java.time.temporal.ChronoUnit.MINUTES

import ars.common.enumeration.{EnumObject, SerializableIntEnumValue}
import ars.kafka.config.{ConsumerConfig, Deserializers, ProducerConfig, Serializers}
import ars.kafka.consumer.retry.RetryPolicy
import ars.kafka.consumer.unpacker.HeaderBodyDeserializationUnpacker
import ars.kafka.consumer.{AbstractDeserializationUnpackingConsumer, ProcessCompletionStatus, ProcessCompletionStatuses}
import ars.kafka.producer.SerializationPackingProducer
import ars.kafka.producer.packer.HeaderBodySerializationPacker

import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.util.{Failure, Success, Try}

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object HeaderedIntegrationTest {

  @SerialVersionUID(1234567890)
  case class TestBody(str: String, int: Int, date: Instant) extends Serializable


  sealed abstract class TestHeader(value: Int) extends SerializableIntEnumValue[TestHeader, TestHeaders.type](value) {
//    def valueOf(code: Int): Try[TestHeader] = TestHeaders.valueOf(code)
  }

  object TestHeaders extends EnumObject[TestHeader, Int] {
    final case object GoodHeader extends TestHeader(1)
    final case object BadHeader extends TestHeader(-1)

    override def values: Seq[TestHeader] = Seq(GoodHeader, BadHeader)
  }

  private[this] def randomHeader(): TestHeader = if (ThreadLocalRandom.current().nextBoolean()) TestHeaders.GoodHeader else TestHeaders.BadHeader

  private[this] val TestTopic = "dm_test_topic_header"

  private[this] val producerConfig = ProducerConfig.defaultLocal(serializers = Serializers.ByteArraySerializers)

  private[this] val producer = new SerializationPackingProducer(
    producerConfig,
    valuePacker = new HeaderBodySerializationPacker[TestHeader, TestBody]
  )

  private[this] val consumerConfig = ConsumerConfig.defaultLocal(deserializers = Deserializers.ByteArrayDeserializers)

  private[this] val consumer = new AbstractDeserializationUnpackingConsumer[AnyRef, (TestHeader, TestBody)](
    consumerConfig, Seq("dm_test_topic_header"),
    valueUnpacker = new HeaderBodyDeserializationUnpacker[TestHeader, TestBody]((t: TestHeader) => true)) {

    override def retryPolicy: RetryPolicy = ??? // TODO

    override def processUnpacked(key: Option[AnyRef], value: (TestHeader, TestBody)): ProcessCompletionStatus = {
      println(s"Receive: '$key' -> '$value'")
      ProcessCompletionStatuses.Success
    }
  }

  def main(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global

    Threads.inSingleThread { () => consumer.start() }

    Threads.inSingleThread { () =>
      (0 until 50).foreach { n =>
        (0 until 20).foreach { i =>
          val key = s"Key $i"
          val header = randomHeader()
          val body = TestBody(s"My string $i", i, Instant.now().plus(i, MINUTES))
          println(s"Send $i: '$key' -> '$header' : '$body'")
          try {
            producer.sendPacked(
              TestTopic, None,
              (header, body)
            ).onComplete {
              case Success(v) => println(v.topic() + " -> " + v.offset() + " -> " + v.timestamp())
              case Failure(e) => println(e)
            }
          } catch {
            case t: Throwable => println(t)
          }
        }
        Thread.sleep(2000)
      }
    }
  }
}
