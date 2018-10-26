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
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait BeforeAfterRecordsProcessing[K, V] extends SingleThreadConsumer[K, V] {

  def beforeRecordsProcess(records: ConsumerRecords[K, V]): ProcessCompletionStatus
  def afterRecordsProcess(records: ConsumerRecords[K, V]): ProcessCompletionStatus

  abstract override def process(records: ConsumerRecords[K, V]): Boolean = {
    // TODO Algorithm
    val beforeStatus = beforeRecordsProcess(records)
    if (beforeStatus != ProcessCompletionStatuses.Success)
      return (beforeStatus == ProcessCompletionStatuses.Success)

    val processStatus = super.process(records)
    if (beforeStatus != ProcessCompletionStatuses.Success) return processStatus

    afterRecordsProcess(records) == ProcessCompletionStatuses.Success
  }
}
