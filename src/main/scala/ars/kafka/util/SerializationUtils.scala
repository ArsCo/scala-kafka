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

import java.io._

import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger

import scala.reflect.ClassTag
import scala.util.{Failure, Try}

/** Serialization/deserialization utility methods.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object SerializationUtils {

  /** Serializer type. */
  type Serializer[T] = (ObjectOutputStream, T) => Unit

  /** Deserializer type. */
  type Deserializer[T] = ObjectInputStream => T

  /**
    * Serializes `value` with custom `serializer`.
    *
    * @param value the value (must be non-null)
    * @param serializer the serializer (must be non-null)
    *
    * @tparam T the value type
    *
    * @return the serializable value (non-null)
    */
  def serialize[T](value: T, serializer: => Serializer[T]): Try[Array[Byte]] = {
    requireNotNull(value, "value")
    requireNotNull(serializer, "serializer")

    Try {
      var oos: ObjectOutputStream = null
      try {
        val baos = new ByteArrayOutputStream(1)
        oos = new ObjectOutputStream(baos)
        serializer(oos, value)
        oos.flush()
        baos.toByteArray

      } finally {

        _closeStreams(oos)
      }
    }
  }

  private def closeStreams(streams: Closeable*): Unit = {
    requireNotNull(streams, "streams")
    requireNotBlank(streams, "streams")

    _closeStreams(streams :_*)
  }


  private def _closeStreams(streams: Closeable*): Unit = {
    if (streams.isEmpty) return

    val stream = streams.head
    if (stream == null) _closeStreams(streams.tail :_*)
    else {
      try {
        stream.close()
      } catch {
        case e: IOException =>
          logger.error("Can't close stream.", e)
      } finally {
        _closeStreams(streams.tail :_*)
      }
    }

  }

  private[this] def tryClose(os: OutputStream): Unit = {

  }



  // TODO: Add all types
  val ByteSerializer: Serializer[Byte] = _.writeByte(_)
  val ShortSerializer: Serializer[Short] = _.writeShort(_)
  val IntSerializer: Serializer[Int] = _.writeInt(_)
  val LongSerializer: Serializer[Long] = _.writeLong(_)
  val StringSerializer: Serializer[String] = _.writeUTF(_)
  def ObjectSerializer[T]: Serializer[T] = _.writeObject(_)

  val ByteDeserializer: Deserializer[Byte] = _.readByte()
  val ShortDeserializer: Deserializer[Short] = _.readShort()
  val IntDeserializer: Deserializer[Int] = _.readInt()
  val LongDeserializer: Deserializer[Long] = _.readLong()
  val StringDeserializer: Deserializer[String] = _.readUTF()
  def ObjectDeserializer[T]: Deserializer[T] = _.readObject().asInstanceOf[T]


//  def ObjectPairSerializer[T1, T2]: Serializer[(T1, T2)] =
//    (obj: (T1, T2), oos: ObjectOutputStream) => {
//
//      oos.writeObject(obj)
//    }

//  def ValueSerializer[T]: Serializer[T] = (value: T, oos: ObjectOutputStream) => {
//    value match {
//      case s: String => StringSerializer
//      case o: AnyRef => ObjectSerializer
//      case v => Failure(new IllegalStateException(s"Unexpected value: '$v' of type '${v.getClass.getCanonicalName}'"))
//    }
//  }

  /**
    * Deserializes `value` with custom `deserializer`.
    *
    * @param bytes the byte array (must be non-null)
    * @param deserializer the deserializer (must be non-null)
    *
    * @tparam T the value type
    *
    * @return the deserializable value (non-null)
    */
  def deserialize[T](bytes: Array[Byte], deserializer: => Deserializer[T]): Try[T] = {
    requireNotNull(bytes, "bytes")
    requireNotNull(deserializer, "deserializer")

    Try {
      var ois: ObjectInputStream = null
      try {
        ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
        deserializer(ois)

      } finally {
        if (ois != null) {
          ois.close()
        }
      }
    }
  }

  /**
    * Serializes string to byte array.
    *
    * @param string the string (must be non-null)
    *
    * @return the byte array (non-null)
    */
  def serializeString(string: String): Try[Array[Byte]] = serialize(string, StringSerializer)

  /**
    * Deserializes string from byte array.
    *
    * @param bytes the byte array (must be non-null)
    *
    * @return the string (non-null)
    */
  def deserializeString(bytes: Array[Byte]): Try[String] = deserialize(bytes, StringDeserializer)


  /**
    * Serializes object (not string) to byte array.
    *
    * @param obj the object (must be non-null)
    *
    * @tparam T the value type
    *
    * @return the byte array (non-null)
    */
  def serializeObject[T <: AnyRef](obj: T): Try[Array[Byte]] = serialize(obj, ObjectSerializer)

  /**
    * Deserializes object (not string) from byte array.
    *
    * @param bytes the byte array (must be non-null)
    *
    * @tparam T the value type
    *
    * @return the object (non-null)
    */
  def deserializeObject[T <: AnyRef](bytes: Array[Byte]): Try[T] = deserialize(bytes, ObjectDeserializer)

  /**
    * Serializes object or string value to byte array.
    *
    * @param value the value (must be non-null)
    *
    * @tparam T the value type
    *
    * @return the byte array (non-null)
    */
  def serializeValue[T](value: T): Try[Array[Byte]] = {
    requireNotNull(value, "value")

    value match {
        // TODO: Add all types
      case b: Byte => serialize(b, ByteSerializer)
      case s: Short => serialize(s, ShortSerializer)
      case i: Int => serialize(i, IntSerializer)
      case l: Long => serialize(l, LongSerializer)
      case s: String => serialize(s, StringSerializer)
      case o: AnyRef => SerializationUtils.serializeObject(o)
      case v => Failure(new IllegalStateException(s"Unexpected value: '$v' of type '${v.getClass.getCanonicalName}'"))
    }
  }

  def deserializeValue[T <: String](bytes: Array[Byte])(implicit classTag: ClassTag[T]): Try[T] = {

    val clazz = classTag.runtimeClass.getClass
    val dv = if(clazz == classOf[String]) deserializeString(bytes)
    else if (clazz == classOf[Byte]) deserialize(bytes, ByteDeserializer)
    else if (clazz == classOf[Short]) deserialize(bytes, ShortDeserializer)
    else if (clazz == classOf[Int]) deserialize(bytes, IntDeserializer)
    else if (clazz == classOf[Long]) deserialize(bytes, LongDeserializer)
    else if (clazz == classOf[String]) deserialize(bytes, StringDeserializer)
    else SerializationUtils.deserializeObject(bytes)

    dv.asInstanceOf[Try[T]]
  }

  private[this] def logger = Logger[SerializationUtils.type]
}
