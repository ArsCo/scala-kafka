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

import ars.kafka.consumer.config.Deserializers.ByteBufferDeserializers
import ars.kafka.consumer.config.Server.DefaultLocalServer


/** Kafka consumer configuration.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait ConsumerConfig {

  /**
    * Gets deserializers.
    *
    * @return the deserializers (non-null)
    */
  def deserializers: Deserializers

  /**
    * Gets consumer `bootstrap.servers` parameter.
    *
    * @return the bootstrap servers (non-blank seq of non-blank elements)
    */
  def bootstrapServers: Seq[Server]

  /**
    * Gets consumer `group.id` parameter.
    *
    * @return the group id (non-blank)
    */
  def groupId: String

  /**
    * Gets consumer `fetch.min.bytes` parameter.
    *
    * @return the minimum amount of data the server should return for a fetch request (non-null)
    */
  def minFetchBytes: Option[Int] = None

  /**
    * Gets consumer `heartbeat.interval.ms` parameter.
    *
    * @return the expected time (ms) between heartbeats to the consumer coordinator when
    *         using Kafka's group management facilities (non-null)
    */
  def heartbeatInterval: Option[Int] = None

  /**
    * Gets consumer `max.partition.fetch.bytes` parameter.
    *
    * @return the maximum amount of data per-partition the server will return (non-null)
    */
  def maxPartitionFetchBytes: Option[Int] = None

  /**
    * Gets consumer `session.timeout.ms` parameter.
    *
    * @return the timeout (ms) used to detect consumer failures when using Kafka's group management facility (non-null)
    */
  def sessionTimeout: Option[Int] = None

  /**
    * Gets consumer `enable.auto.commit` parameter.
    *
    * @return if `true` the consumer's offset will be periodically committed in the background.
    */
  def autoCommit: Option[Boolean] = None

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
  def all: Map[String, Any] = {
    toMap(deserializers) ++ toMap(bootstrapServers) ++ Map("group.id" -> groupId) ++
      minFetchBytes.map("fetch.min.bytes" -> _) ++
      heartbeatInterval.map("heartbeat.interval.ms" -> _) ++
      maxPartitionFetchBytes.map("max.partition.fetch.bytes" -> _) ++
      sessionTimeout.map("session.timeout.ms" -> _) ++
      autoCommit.map("enable.auto.commit" -> _) ++
      raw
  }

  private def toMap(deserializers: Deserializers): Map[String, Any] = {
    Map(
      "key.deserializer" -> deserializers.key,
      "value.deserializer" -> deserializers.value
    )
  }

  private def toMap(servers: Seq[Server]): Map[String, Any] = {
    val serversString = servers.map { case Server(h, p) => s"$h:$p" }.mkString(",")
    Map(
      "bootstrap.servers" -> serversString
    )
  }
}

object ConsumerConfig {

  final val DefaultGroupId = "default-group"

  /**
    * Creates local consumer Kafka configuration.
    *
    * @param deserializers the deserializers (non-null).
    *                      By default it uses binary deserializers for both key and value.
    * @param bootstrapServers the bootstrap servers (non-blank)
    *                         By default it uses `localhost:`
    * @param groupId the group id (non-blank)
    *                By default it uses string `defaultGroup`
    *
    * @throws IllegalArgumentException if any argument is invalid
    *
    * @return the new instance of configuration.
    */
  def defaultLocal(
      deserializers: Deserializers = ByteBufferDeserializers,
      bootstrapServers: Seq[Server] = Seq(DefaultLocalServer),
      groupId: String = DefaultGroupId
  ): ConsumerConfig = {
    DefaultConsumerConfig(deserializers, bootstrapServers, groupId)
  }

  def apply(
    deserializers: Deserializers,
    bootstrapServers: Seq[Server],
    groupId: String,

    minFetchBytes: Option[Int] = None,
    heartbeatInterval: Option[Int] = None,
    maxPartitionFetchBytes: Option[Int] = None,
    sessionTimeout: Option[Int] = None,

    autoCommit: Option[Boolean] = None,
    raw: Map[String, Any] = Map()
  ): ConsumerConfig = {
    DefaultConsumerConfig(deserializers, bootstrapServers, groupId, minFetchBytes,
      heartbeatInterval, maxPartitionFetchBytes, sessionTimeout, autoCommit, raw)
  }
}
