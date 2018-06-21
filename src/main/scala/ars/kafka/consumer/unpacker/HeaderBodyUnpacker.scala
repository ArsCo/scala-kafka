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

package ars.kafka.consumer.unpacker

import scala.util.{Failure, Success, Try}
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait HeaderBodyUnpacker[From, Header, Body] extends Unpacker[From, (Header, Body)] {

  override def unpack(from: From): Try[(Header, Body)] = {
    requireNotNull(from, "from")

    // TODO: Rewrite as functional algorithm
    split(from) match {
      case Success((headerBytes, bodyBytes)) =>
        unpackHeader(headerBytes) match {
          case Success(header) =>
            if (isUnpackBody(header)) {
              unpackBody(bodyBytes) match {
                case Success(body) =>
                  Success((header, body))

                case Failure(e) =>
                  logger.error("Can't unpack body")
                  Failure(e)
              }
            } else {
              logger.info("Don't unpack body. It was skipped.")
              Failure(new IllegalArgumentException("Body was skipped")) // TODO: Exception type
            }

          case Failure(e) =>
            logger.error("Can't unpack header.")
            Failure(e)
        }

      case Failure(e) =>
        logger.error("Can't split value to header and body bytes.")
        Failure(e)
    }

  }

  def split(from: From): Try[(From, From)]

  def unpackHeader(headerBytes: From): Try[Header]

  def unpackBody(bodyBytes: From): Try[Body]


  def isUnpackBody(header: Header): Boolean



  private[this] val logger = Logger[HeaderBodyUnpacker[_, _, _]]

}
