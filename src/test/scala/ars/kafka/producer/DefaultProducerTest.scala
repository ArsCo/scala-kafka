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

import ars.kafka.AbstractBaseTest
import ars.kafka.config.{DefaultProducerConfig, Serializers, Server}
import ars.kafka.producer.Producer.withProducer

/** Tests for [[DefaultProducer]].
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class DefaultProducerTest extends AbstractBaseTest {

}


object DefaultProducer {


//  def test(): Unit = {
//
//    val config = new DefaultProducerConfig(
//      serializers = Serializers.ByteBufferSerializers,
//      bootstrapServers = Seq(Server("localhost", 9092))
//    )
//
//    val producer = new DefaultProducer[String, String](
//      config
//    )
//
//    //    producer { p =>
//    //      p.send("", "sdf", "sdfsdf")
//    //    }
//
//
//    withProducer[String, String](config){ p =>
//      p.send("", "sdf", "sdfsdf")
//    }
//
//  }
}