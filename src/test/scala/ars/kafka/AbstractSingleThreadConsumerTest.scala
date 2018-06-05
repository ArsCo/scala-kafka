///*
// * Copyright 2018 Arsen Ibragimov (ars)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *         http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package ars.kafka
//import ars.kafka.consumer.AbstractSingleThreadConsumer
//import org.apache.kafka.clients.consumer.ConsumerRecord
//
///** Tests for [[AbstractSingleThreadConsumer]]
//  *
//  * @author Arsen Ibragimov (ars)
//  * @since 0.0.1
//  */
//class AbstractSingleThreadConsumerTest extends AbstractBaseTest {
//  "AbstractSingleThreadConsumer" must "validate args" in {
//    val m = Map("key" -> "value")
//
//    new AbstractSingleThreadConsumer(m, "topic") {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//    }
//
//    new AbstractSingleThreadConsumer(m, "topic", 99) {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(null, "topic") {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(null, "topic", 99) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(Map(), "topic") {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(Map(), "topic", 99) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, null) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, null, 99) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "") {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "", 99) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "  ") {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "  ", 99) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "topic", -100) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "topic", -1) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "topic", 0) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "topic", 1) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//
//    intercept[IllegalArgumentException] {
//      new AbstractSingleThreadConsumer(m, "topic", 50) {
//        override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//      }
//    }
//  }
//
//  it must "have default pollTimeout" in {
//    val m = Map("key" -> "value")
//    val c = new AbstractSingleThreadConsumer(m, "topic") {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = ???
//    }
//    assertResult(AbstractSingleThreadConsumer.DefaultPollingTimeout)(c.pollTimeout)
//  }
//
//  "start()" must "starts consuming" in {
//    fail()
//  }
//
//  it must "throws ISE if consuming already started" in {
//    val m = Map("key" -> "value")
//    val c = new AbstractSingleThreadConsumer(m, "topic") {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = true
//    }
//    c.start()
//    intercept[IllegalStateException] {
//      c.start()
//    }
//  }
//
//  "process(ConsumerRecords[K, V])" must "iterate records until unsuccess process(ConsumerRecord[K, V]) and return `false`" in {
//
//    val c = new AbstractSingleThreadConsumer[String, String]() {}
//  }
//
//  it must "iterate all records and return `true` if all process(ConsumerRecord[K, V]) was completed successfully" in {
//
//  }
//
//  it must "return `false` if there's a process(ConsumerRecord[K, V]) that throws exception" in {
//
//  }
//
//  "stop()" must "stop already running consumer" in {
//
//  }
//
//  it must "throw ISE if consuming was not started yet" in {
//    val m = Map("key" -> "value")
//    val c = new AbstractSingleThreadConsumer(m, "topic") {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = true
//    }
//    intercept[IllegalStateException] {
//      c.stop()
//    }
//  }
//
//  it must "throw ISE if consuming was already stopped" in {
//    val m = Map("key" -> "value")
//    val c = new AbstractSingleThreadConsumer(m, "topic") {
//      override def process(record: ConsumerRecord[Nothing, Nothing]): Boolean = true
//    }
//    c.start()
//    c.stop()
//    intercept[IllegalStateException] {
//      c.stop()
//    }
//  }
//}
