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

/** The compression type.
  *
  * @param code the value for configuration
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract sealed class CompressionType(val code: String) extends EnumValue[String]

object CompressionTypes extends EnumObject[CompressionType, String] {
  final case object None extends CompressionType("none")
  final case object GZip extends CompressionType("gzip")
  final case object Snappy extends CompressionType("snappy")
  final case object LZ4 extends CompressionType("lz4")

  val values: Seq[CompressionType] = Seq(None, GZip, Snappy, LZ4)
}
