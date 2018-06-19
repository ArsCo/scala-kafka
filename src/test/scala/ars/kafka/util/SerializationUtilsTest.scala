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

package ars.kafka.util

import java.io.{ObjectInput, ObjectInputStream, ObjectOutputStream}

import ars.kafka.AbstractBaseTest


/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class SerializationUtilsTest extends AbstractBaseTest {

  "serialize()" must "validate args" in {

    intercept[IllegalArgumentException] {
      SerializationUtils.serialize(null, (s: String, o: ObjectOutputStream) => {})
    }

    intercept[IllegalArgumentException] {
      SerializationUtils.serialize("String", null)
    }
  }

  it must "serialize value with `serializer`" in {
    val v = TestObject("string", 123)

    val ser = SerializationUtils.serializeObject(v)
    assert(ser != null && ser.isSuccess)
  }

//  "deserialize()" must "validate args" in {
//
//    intercept[IllegalArgumentException] {
//      SerializationUtils.deserialize(null, (a: Array[Byte], o: ObjectInputStream) => {})
//    }
//
//    intercept[IllegalArgumentException] {
//      SerializationUtils.deserialize(new Array[Byte](5), null)
//    }
//  }

  "serializeObject() and deserializeObject()" must "work together" in {
    val v = TestObject("string", 123)

    val ser = SerializationUtils.serializeObject(v)
    assert(ser != null && ser.isSuccess)

    val des = SerializationUtils.deserializeObject(ser.get)
    assert(des != null && des.isSuccess)
    assertResult(v)(des.get)
  }
}

case class TestObject(s: String, i: Int) // Do not place inside test class
