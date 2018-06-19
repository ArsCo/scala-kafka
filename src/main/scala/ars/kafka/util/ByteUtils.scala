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

import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import ars.precondition.require.Require.Default._

/** Byte utility methods.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object ByteUtils {

  /**
    * Converts integer `value` to 4 bytes little-endian byte array.
    *
    * @param value the integer value
    *
    * @return the byte array (4 bytes, non-null)
    */
  def int2bytes(value: Int): Array[Byte] =
    ByteBuffer.allocate(4).order(LITTLE_ENDIAN).putInt(value).array()

  /** Converts 4 elements little-endian byte array to integer value.
    *
    * @param bytes the bytes (must be non-null, 4 bytes)
    *
    * @throws IllegalArgumentException if `bytes` length is not 4
    *
    * @return the integer
    */
  def bytes2int(bytes: Array[Byte]): Int = {
    requireSize(bytes, 4)
    ByteBuffer.wrap(bytes).order(LITTLE_ENDIAN).getInt()
  }
}
