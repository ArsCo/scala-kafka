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

package ars.kafka.config

import ars.common.enumeration.{EnumObject, EnumValue}

/** The producer ack.
  *
  * @param code the code for configuration (non-null)
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
sealed abstract class ProducerAck(override val code: String) extends EnumValue[String]

object ProducerAcks extends EnumObject[ProducerAck, String] {
  final case object NotWait extends ProducerAck("0")
  final case object LeaderOnly extends ProducerAck("1")
  final case object All extends ProducerAck("all")

  /** @inheritdoc */
  val values = Seq(NotWait, LeaderOnly, All)
}
