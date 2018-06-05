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

import java.io._
import java.nio.ByteBuffer
import java.time.Instant
import java.time.temporal.ChronoUnit

import ars.kafka.config.{ConsumerConfig, ProducerConfig}
import ars.kafka.consumer.{AbstractUnpackingSingleThreadConsumer, Unpacker}
import ars.kafka.producer.{AbstractPackingProducer, Packer}

import scala.util.{Failure, Success, Try}

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object IntegrationTest {

  @SerialVersionUID(1234567890)
  case class TestMessage(str: String, int: Int, date: Instant) extends Serializable

  private[this] val TestTopic = "dm_test_topic"

  private[this] val producerConfig = ProducerConfig.defaultLocal()

  private[this] val producer = new AbstractPackingProducer[String, ByteBuffer, TestMessage, ByteBuffer](
    producerConfig,
    new Packer[String, ByteBuffer] {
      override def pack(from: String): Try[ByteBuffer] = {

        var ois: ObjectOutputStream = null
        try {
          val baos = new ByteArrayOutputStream(1)
          ois = new ObjectOutputStream(baos)
          ois.writeUTF(from)
          ois.flush()
          ois.flush()
          ois.flush()
          ois.flush()


          val array = baos.toByteArray
          Success(ByteBuffer.wrap(array))

        } finally {
          ois.close()
        }
      }
    },
    new Packer[TestMessage, ByteBuffer] {
      override def pack(from: TestMessage): Try[ByteBuffer] = {
        var ois: ObjectOutputStream = null
        try {
          val baos = new ByteArrayOutputStream(1)
          ois = new ObjectOutputStream(baos)
          ois.writeObject(from)
          ois.flush()
          ois.flush()
          ois.flush()
          ois.flush()

          Success(ByteBuffer.wrap(baos.toByteArray))

        } finally {
          ois.close()
        }
      }
    }
  )

  private[this] val consumerConfig = ConsumerConfig.defaultLocal()

  private[this] val consumer = new AbstractUnpackingSingleThreadConsumer[ByteBuffer, String, ByteBuffer, TestMessage](
    consumerConfig,
    Seq(TestTopic),
    new Unpacker[ByteBuffer, String] {
      override def unpack(from: ByteBuffer): Try[String] = {
        var ois: ObjectInputStream = null
        try {

          val bytes = from.array()
          val baos = new ByteArrayInputStream(bytes)
          ois = new ObjectInputStream(baos)
          val s = ois.readUTF()
          Success(s)

        } finally {
          ois.close()
        }
      }
    },
    new Unpacker[ByteBuffer, TestMessage] {
      override def unpack(from: ByteBuffer): Try[TestMessage] = {
        var ois: ObjectInputStream = null
        try {
          val baos = new ByteArrayInputStream(from.array())
          ois = new ObjectInputStream(baos)
          val s = ois.readObject().asInstanceOf[TestMessage]
          Success(s)

        } finally {
          ois.close()
        }
      }
    }
  ) {
    override def processUnpacked(key: Option[String], value: TestMessage): Boolean = {
      println(s"Receive: '$key' -> '$value'")
      true
    }
  }

  def main(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global


    (0 until 20).foreach { i =>
      val key = s"Key $i"
      val message = TestMessage(s"My string $i", i, Instant.now().plus(i, ChronoUnit.MINUTES))
      println(s"Send $i: '$key' -> '$message'")
      producer.sendPacked(
        TestTopic, None,
        message
      ).onComplete {
        case Success(v) => println(v.topic() + " -> " + v.offset() + " -> " + v.timestamp())
        case Failure(e) => println(e)
      }
    }

    consumer.start()
  }
}
