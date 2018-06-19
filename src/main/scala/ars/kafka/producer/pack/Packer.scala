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

package ars.kafka.producer.pack

import scala.util.{Success, Try}

/** Packer.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait Packer[From, To] {

  /**
    * Packs the `from` value of type [[From]] to serial type [[To]]
    *
    * @param from the from value (must be non-null).
    *
    * @return the result (non-null)
    */
  def pack(from: From): Try[To]
}

object Packer {

  /**
    * Creates new identity packer. This unpacker do noting.
    *
    * @tparam T the type param
    *
    * @return the new identity unpacker (non-null)
    */
  def identityPacker[T](): Packer[T, T] = new Packer[T, T] {

    /** @inheritdoc */
    override def pack(from: T): Try[T] = Success(from)
  }
}
