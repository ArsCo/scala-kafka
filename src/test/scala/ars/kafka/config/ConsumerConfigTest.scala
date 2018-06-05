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

import ars.kafka.AbstractBaseTest

/** Tests for [[ConsumerConfig]]
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class ConsumerConfigTest extends AbstractBaseTest {

  private val c = new ConsumerConfig {

    override def deserializers: Deserializers = ???
    override def bootstrapServers: Seq[Server] = ???
    override def groupId: String = ???
  }

  "ConsumerConfig" must "have correct interface" in {
    assertResult(None)(c.minFetchBytes)
    assertResult(None)(c.heartbeatInterval)
    assertResult(None)(c.maxPartitionFetchBytes)
    assertResult(None)(c.sessionTimeout)
    assertResult(None)(c.autoCommit)
  }
}
