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
import java.time.temporal.ChronoUnit.MINUTES
import java.util.concurrent.{ExecutorService, Executors}

import ars.kafka.config.{ConsumerConfig, ProducerConfig}
import ars.kafka.consumer.retry.RetryPolicy
import ars.kafka.consumer.unpacker.Unpacker
import ars.kafka.consumer.{AbstractUnpackingConsumer, ProcessCompletionStatus, ProcessCompletionStatuses}
import ars.kafka.producer.DefaultPackingProducer
import ars.kafka.producer.packer.{Packer, SimpleSerializationPacker}

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

  private[this] val producer = new DefaultPackingProducer[String, Array[Byte], TestMessage, ByteBuffer](
    producerConfig,
    new SimpleSerializationPacker[String],

//    new Packer[String, ByteBuffer] {
//      override def pack(from: String): Try[ByteBuffer] = {
//
//        var ois: ObjectOutputStream = null
//        try {
//          val baos = new ByteArrayOutputStream(1)
//          ois = new ObjectOutputStream(baos)
//          ois.writeUTF(from)
//          ois.flush()
//
//
//          val array = baos.toByteArray
//          Success(ByteBuffer.wrap(array))
//
//        } finally {
//          ois.close()
//        }
//      }
//    },
    new Packer[TestMessage, ByteBuffer] {
      override def pack(from: TestMessage): Try[ByteBuffer] = {
        var ois: ObjectOutputStream = null
        try {
          val baos = new ByteArrayOutputStream(1)
          ois = new ObjectOutputStream(baos)
          ois.writeObject(from)
          ois.flush()

          Success(ByteBuffer.wrap(baos.toByteArray))

        } finally {
          ois.close()
        }
      }
    }
  )

  private[this] val consumerConfig = ConsumerConfig.defaultLocal()

  private[this] val consumer = new AbstractUnpackingConsumer[ByteBuffer, String, ByteBuffer, TestMessage](
    consumerConfig,
    Seq(TestTopic),
    new Unpacker[ByteBuffer, String] {
      override def unpack(from: ByteBuffer): Try[String] = {
        //        var ois: ObjectInputStream = null
        //        try {
        //
        //          val bytes = from.array()
        //          val baos = new ByteArrayInputStream(bytes)
        //          ois = new ObjectInputStream(baos)
        //          val s = ois.readUTF()
        //          Success(s)
        //
        //        } finally {
        //          ois.close()
        //        }
        //      }
        Success("dfsdf")
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
    override def processUnpacked(key: Option[String], value: TestMessage): ProcessCompletionStatus = {
      println(s"Receive: '$key' -> '$value'")
      ProcessCompletionStatuses.Success
    }

    override def retryPolicy: RetryPolicy = ??? // TODO
  }

  def main(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global

    Threads.inSingleThread { () => consumer.start() }

    Threads.inSingleThread { () =>
      (0 until 50).foreach { n =>
        (0 until 20).foreach { i =>
          val key = s"Key $i"
          val message = TestMessage(s"My string $i", i, Instant.now().plus(i, MINUTES))
          println(s"Send $i: '$key' -> '$message'")
          producer.sendPacked(
            TestTopic, None,
            message
          ).onComplete {
            case Success(v) => println(v.topic() + " -> " + v.offset() + " -> " + v.timestamp())
            case Failure(e) => println(e)
          }
        }
        Thread.sleep(2000)
      }
    }





//    withExecutor(() => Executors.newSingleThreadExecutor()) { executor =>
//
//      executor.submit(
//        new Runnable {
//          override def run(): Unit = {
//
//          }
//        }
//      )
//
//
//    }


  }





}


object Threads {
  def withExecutor(creator: => () => ExecutorService)(block: => () => Unit): Unit = {
    var stp: ExecutorService  = null
    try {
      stp = creator()
      stp.submit(new Runnable { override def run(): Unit = { block() } })
    } finally { // TODO Catch Interrupted
      if (stp != null) {
        stp.shutdown()
//        stp.awaitTermination(10, TimeUnit.MINUTES)
      }
    }
  }

  def
  inSingleThread(block: => () => Unit): Unit = {
    withExecutor(() => Executors.newSingleThreadExecutor())(block)
  }
}
