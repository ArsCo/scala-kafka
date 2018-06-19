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

package ars.kafka.producer.pack.serialization

import java.io.{IOException, ObjectInputStream, ObjectOutputStream, ObjectStreamException}

import scala.util.{Failure, Success, Try}

trait EnumValueOf


/** Serializable Scala enumeration value. There're two subclasses [[SerializableIntEnum]] fro integer codes
  * and [[SerializableStringEnum]] for string codes.
  *
  * Example:
  * {{{
  *   sealed abstract class MyEnumValue(val code: Int) extends SerializableIntEnum[MyEnumValue] {
  *     def valueOf(code: Int): Try[MyEnumValue] = MyEnumValues.valueByCode(code)
  *   }
  *
  *   object MyEnumValues {
  *     final case object FirstValue extends MyEnumValue(1)
  *     final case object SecondValue extends MyEnumValue(2)
  *     final case object ThirdValue extends MyEnumValue(3)
  *
  *     def valueByCode(code: Int): Try[MyEnumValue] = {
  *       code match {
  *         case FirstValue.code  => FirstValue
  *         case SecondValue.code => SecondValue
  *         case ThirdValue.code  => ThirdValue
  *         case _ => Failure(new IllegalArgumentException("Unknown enumeration code"))
  *       }
  *     }
  *   }
  * }}}
  *
  * @tparam EnumType the enumeration value type
  * @tparam CodeType the code type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait SerializableEnum[EnumType, CodeType] extends Serializable {

  protected[this] var serializationCode: CodeType = _

  /**
    * Code.
    *
    * @return the code for current value.
    */
  def code: CodeType

  /**
    * Gets the value by `code`.
    *
    * @param code the code for current value
    *
    * @return the value (non-null)
    */
  def valueOf(code: CodeType): Try[EnumType]

  /**
    * Wraps exception in runtime exception if it's not subtype of [[RuntimeException]].
    *
    * @param t the throwable (non-null)
    *
    * @return the [[RuntimeException]]
    */
  protected def wrapInRuntimeException(t: Throwable): RuntimeException = {
    t match {
      case r: RuntimeException => r
      case e => new RuntimeException(e)
    }
  }

  @throws[IOException]
  def writeObject(out: ObjectOutputStream): Unit = serialize(out)

  @throws[IOException]
  @throws[ClassNotFoundException]
  def readObject(in: ObjectInputStream): Unit = deserialize(in)

  /**
    * Serializes code to stream.
    *
    * @param out the output stream (non-null)
    */
  def serialize(out: ObjectOutputStream): Unit

  /**
    * Deserializes code from stream.
    *
    * @param in the input stream (non-null)
    *
    * @return the code (non-null)
    */
  def deserialize(in: ObjectInputStream): CodeType

  @throws[ObjectStreamException]
  def readResolve(): Any = {
    valueOf(code) match {
      case Success(v) => v
      case Failure(e) => throw wrapInRuntimeException(e)
    }
  }
}


/** Serializable to `Int` Scala enumeration value.
  *
  * @tparam EnumType the enumeration value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class SerializableIntEnum[EnumType](val code: Int)
  extends SerializableEnum[EnumType, Int] with Serializable {

  def serialize(out: ObjectOutputStream): Unit = out.writeInt(code)
  def deserialize(in: ObjectInputStream): Int = in.readInt()
}


/** Serializable to [[String]] Scala enumeration value.
  *
  * @tparam EnumType the enumeration value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract class SerializableStringEnum[EnumType](val code: String)
  extends SerializableEnum[EnumType, String] with Serializable {

  def serialize(out: ObjectOutputStream): Unit = out.writeUTF(code)
  def deserialize(in: ObjectInputStream): String = in.readUTF()
}
