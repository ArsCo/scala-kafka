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

package ars.kafka.consumer.config

import ars.precondition.require.Require.Default._

/** Kafka consumer configuration.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class DefaultConsumerConfig(
    override val deserializers: Deserializers,
    override val bootstrapServers: Seq[Server],
    override val groupId: String,

    override val minFetchBytes: Option[Int] = None,
    override val heartbeatInterval: Option[Int] = None,
    override val maxPartitionFetchBytes: Option[Int] = None,
    override val sessionTimeout: Option[Int] = None,

    override val autoCommit: Option[Boolean] = None,
    override val raw: Map[String, Any] = Map()
) extends ConsumerConfig {

  requireNotNull(deserializers, "deserializers")
  requireNotBlank(bootstrapServers, "bootstrapServers")
  requireAllNotNull(bootstrapServers, "bootstrapServers")
  requireNotBlank(groupId, "groupId")

//  optional(minFetchBytes, "minFetchBytes") { case (v, n) =>
//    requirePositive(v, n)
//  }
//
//  optional(heartbeatInterval, "heartbeatInterval") { case (v, n) =>
//    requirePositive(v, n)
//  }
//
//  optional(maxPartitionFetchBytes, "maxPartitionFetchBytes") { case (v, n) =>
//    requirePositive(v, n)
//  }
//
//  optional(sessionTimeout, "sessionTimeout") { case (v, n) =>
//    requirePositive(v, n)
//  }
//
//  optional(autoCommit, "autoCommit")(requireNotNull)
//
//  requireRaw()
//
//  def requireRaw() {
//    requireNotNull(raw, "raw")
//
//    val inter = raw.keySet.intersect(Custom)
//    val interString = "[" + inter.mkString(", ") + "]"
//    require(inter.nonEmpty, s"Parameter `raw` must not contains $interString")
//  }




  private val Custom = Set(
    "key.deserializer",
    "value.deserializer",

    "bootstrap.servers",
    "group.id",
    "fetch.min.bytes",
    "heartbeat.interval.ms",
    "max.partition.fetch.bytes",
    "session.timeout.ms",
    "enable.auto.commit"
  )
}





// TODO
//case class SslKey(password: String) {
//  password.map("ssl.key.password" -> _)
//}
//
//case class SslKeystore(location: String, password: String) {
//  def toMap: Map[String, Any] = {
//    location.map("ssl.keystore.location" -> _) ++
//    password.map("ssl.keystore.password" -> _)
//
//  }
//}
//
//case class SslTruststore(location: String, password: String) {
//  location.map("ssl.truststore.location" -> _) ++
//  password.map("ssl.truststore.password" -> _)
//}

