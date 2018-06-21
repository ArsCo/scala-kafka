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

import ars.precondition.require.Require.Default._

/** Default implementation of [[ConsumerConfig]].
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class DefaultConsumerConfig(
    /** @inheritdoc */
    override val deserializers: Deserializers,

    /** @inheritdoc */
    override val bootstrapServers: Seq[Server],

    /** @inheritdoc */
    override val groupId: String,

    /** @inheritdoc */
    override val minFetchBytes: Option[Int] = None,

    /** @inheritdoc */
    override val heartbeatInterval: Option[Int] = None,

    /** @inheritdoc */
    override val maxPartitionFetchBytes: Option[Int] = None,

    /** @inheritdoc */
    override val sessionTimeout: Option[Int] = None,

    /** @inheritdoc */
    override val autoCommit: Option[Boolean] = None,

    /** @inheritdoc */
    override val raw: Map[String, Any] = Map()
) extends ConsumerConfig {

  requireNotNull(deserializers, "deserializers")
  requireNotBlank(bootstrapServers, "bootstrapServers")
  requireAllNotNull(bootstrapServers, "bootstrapServers")
  requireNotBlank(groupId, "groupId")

  // TODO: Add correct validation code

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

//  private val Custom = Set(
//    "key.deserializer",
//    "value.deserializer",
//
//    "bootstrap.servers",
//    "group.id",
//    "fetch.min.bytes",
//    "heartbeat.interval.ms",
//    "max.partition.fetch.bytes",
//    "session.timeout.ms",
//    "enable.auto.commit"
//  )
}
