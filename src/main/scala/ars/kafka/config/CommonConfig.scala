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

import java.util.Properties

import ars.kafka.util.KafkaUtils
import ars.precondition.require.Require.Default._

/** The common configuration params of Kafka consumer and producer.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait CommonConfig {

  /**
    * Gets all params that has no explicit getter methods as a map of key/value pairs.
    *
    * @return the map containing all params that has no explicit getter methods (non-null)
    */
  def raw: Map[String, Any] = Map()

  /**
    * Gets all params as a map of key/value pairs.
    *
    * @return the map containing all params (non-null)
    */
  def all: Map[String, Any] = raw

  /**
    * Converts sequence of servers to comma separated list of `<IP>:<Port>` pairs.
    *
    * @param servers the servers (must be non-null)
    *
    * @throws IllegalArgumentException if `servers` is blank
    *
    * @return the configuration params map
    */
  protected final def toMap(servers: Server*): Map[String, Any] = {
    requireNotBlank(servers, "servers")

    val serversString = servers.map { case Server(hostname, port) => s"$hostname:$port" }.mkString(",")
    Map("bootstrap.servers" -> serversString)
  }

  /**
    * Converts sequence of pairs `(<key>, <optional>)` to configuration params map.
    * It filters [[None]] values.
    *
    * @param params the sequence of pairs `(<key>, <optional>)` (must be non-null)
    *
    * @throws IllegalArgumentException if `params` is null or if any of keys is empty
    *                                  or if one of values is null or `Some(null)`
    *
    * @return the configuration params map
    */
  protected final def optionalsToMap(params: (String, Option[Any])*): Map[String, Any] = {
    val keys = params.map(_._1)
    val values = params.map(_._2)
    val definedValues = values.filter(_.isDefined).map(_.get)

    requireNotNull(params, "params")
    requireAllNotBlank(keys, "params._1")
    requireAllNotNull(values, "params._2")
    requireAllNotNull(definedValues, "params._2")

    params.filter(_._2.isDefined).map { case (k, v) => (k, v.get) }.toMap
  }

  /**
    * @return the same as [[all]] but returns Java [[Properties]].
    */
  def allAsProps: Properties = KafkaUtils.toProps(all)

}
