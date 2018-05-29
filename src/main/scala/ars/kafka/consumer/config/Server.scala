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

import ars.precondition.implicits._
import ars.precondition.require.Require.Default._

/** Server config part.
  *
  * @param host the host (must be non-blank)
  * @param port the port (must be in range [0, 0xffff])
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
case class Server(host: String, port: Int) {
  requireNotBlank(host, "host")
  requireNumberInRange(port, 0.inclusive, 0xffff.inclusive, "port")
}
object Server {
  final val DefaultLocalServer = Server("localhost", 9092)
}