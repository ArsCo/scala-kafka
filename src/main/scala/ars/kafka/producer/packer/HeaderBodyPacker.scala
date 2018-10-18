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

package ars.kafka.producer.packer

import ars.precondition.require.Require.Default._

import scala.util.Try

/** Packer to pack header-body message. It packs header, packs body and then
  * concatenates them to single binary sequence.
  *
  * @tparam Header the header type
  * @tparam Body the body type
  * @tparam To the to value type
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait HeaderBodyPacker[Header, Body, To] extends Packer[(Header, Body), To] {

  override def pack(from: (Header, Body)): Try[To] = {
    requireNotNull(from, "from")
    requireNotNull(from._1, "from._1")
    requireNotNull(from._2, "from._2")

    val (header, body) = from
    pack(header, body)
  }

  /**
    * Packs header, packs body and concatenates them. It must call
    * [[packHeader()]] to pack header and call [[packBody()]] to pack body.
    *
    * @param header the header (must be non-null)
    * @param body the body (must be non-null)
    *
    * @return
    */
  def pack(header: Header, body: Body): Try[To]

  /** Packs `body` to [[To]].
    *
    * @param body the body (must be non-null)
    *
    * @return the packed body
    */
  def packBody(body: Body): Try[To]

  /** Packs `header` to [[To]].
    *
    * @param header the header (must be non-null)
    *
    * @return the packed header
    */
  def packHeader(header: Header): Try[To]
}
