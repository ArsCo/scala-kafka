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

/** Kafka producer configuration.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class DefaultProducerConfig(
    /** @inheritdoc */
    override val serializers: Serializers,

    /** @inheritdoc */
    override val bootstrapServers: Seq[Server],

    /** @inheritdoc */
    override val asks: Option[ProducerAck] = None,

    /** @inheritdoc */
    override val memoryBuffer: Option[Long] = None,

    /** @inheritdoc */
    override val compressionType: Option[CompressionType] = None,

    /** @inheritdoc */
    override val retries: Option[Int] = None,

    /** @inheritdoc */
    override val idempotence: Option[Boolean] = None,

    /** @inheritdoc */
    override val sslKeyPassword: Option[String] = None,

    /** @inheritdoc */
    override val sslKeystore: Option[Ssl] = None,

    /** @inheritdoc */
    override val sslTruststore: Option[Ssl] = None,

    /** @inheritdoc */
    override val batchSize: Option[Int] = None,

    /** @inheritdoc */
    override val raw: Map[String, Any] = Map()
) extends ProducerConfig {

  requireNotNull(serializers, "serializers")

  requireNotBlank(bootstrapServers, "bootstrapServers")
  requireAllNotNull(bootstrapServers, "bootstrapServers")

  requireNotNull(asks, "asks")
  requireAllNotNull(asks, "asks")

  requireNotNull(memoryBuffer, "memoryBuffer")
  optional(memoryBuffer, "memoryBuffer")(requireNonNegative)

  requireNotNull(compressionType, "compressionType")
  requireAllNotNull(compressionType, "compressionType")

  requireNotNull(retries, "retries")
  optional(retries, "retries")(requireNonNegative)

  requireNotNull(idempotence, "idempotence")
  optional(idempotence, "idempotence")(requireNotNull)

  requireNotNull(sslKeyPassword, "sslKeyPassword")
  requireAllNotBlank(sslKeyPassword, "sslKeyPassword")

  requireNotNull(sslKeystore, "sslKeystore")
  requireAllNotNull(sslKeystore, "sslKeystore")

  requireNotNull(sslTruststore, "sslTruststore")
  requireAllNotNull(sslTruststore, "sslTruststore")

  requireNotNull(batchSize, "batchSize")
  optional(batchSize, "batchSize")(requireNonNegative)

  // TODO: Check validation code
}
