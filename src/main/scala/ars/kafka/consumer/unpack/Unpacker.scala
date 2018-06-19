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

package ars.kafka.consumer.unpack

import scala.util.{Success, Try}

/** Unpacker.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait Unpacker[From, To] {

  /**
    * Unpacks the `from` value of type [[From]] to serial type [[To]]
    *
    * @param from the from value (must be non-null).
    *
    * @return the result (non-null)
    */
  def unpack(from: From): Try[To]
}

object Unpacker {

  /**
    * Creates new identity unpacker. This unpacker do noting.
    *
    * @tparam T the type param
    *
    * @return the new identity unpacker (non-null)
    */
  def identityUnpacker[T](): Unpacker[T, T] = new Unpacker[T, T] {

    /** @inheritdoc */
    override def unpack(from: T): Try[T] = Success(from)
  }
}
