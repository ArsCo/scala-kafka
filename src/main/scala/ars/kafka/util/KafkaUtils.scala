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

import java.util.Properties

import ars.precondition.require.Require.Default._

object KafkaUtils {

  /**
    * Converts map of kafka properties to `Properties`.
    *
    * @param map the map (must be non-null)
    *
    * @return the new  `Properties` (non-null)
    */
  def toProps(map: Map[String, Any]): Properties = {
    requireNotNull(map, "map")

    val props = new Properties()
    map.map { case (k, v) => props.setProperty(k, v.toString) }
    props
  }
}
