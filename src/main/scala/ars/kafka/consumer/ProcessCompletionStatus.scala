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

package ars.kafka.consumer

/** Processing completion status.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
abstract sealed class ProcessCompletionStatus(value: String)

object ProcessCompletionStatuses {
  final case object Skip extends ProcessCompletionStatus("skip")
  final case object Success extends ProcessCompletionStatus("success")
  final case object Retry extends ProcessCompletionStatus("retry")
  final case object Policy extends ProcessCompletionStatus("policy") // TODO
}
