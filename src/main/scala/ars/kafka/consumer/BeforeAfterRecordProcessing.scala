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

import org.apache.kafka.clients.consumer.ConsumerRecord

/** Methods to add before and after record processing code.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait BeforeAfterRecordProcessing[K, V] extends SingleThreadConsumer[K, V] {

  /**
    * This method will be called before `record` processing
    * (before [[SingleThreadConsumer.process(]] method).
    *
    * @param record the record (non-null)
    *
    * @return `true` if record must be processed in [[process()]], and `false` otherwise
    */
  def beforeProcess(record: ConsumerRecord[K, V]): ProcessCompletionStatus

  /**
    * This method will be called before `record` processing
    * (after [[SingleThreadConsumer.process(]] method).
    *
    * @param record the record (non-null)
    *
    * @return `true` if record is consumed (must be committed), and `false` otherwise
    */
  def afterProcess(record: ConsumerRecord[K, V]): ProcessCompletionStatus

  /** @inheritdoc */
  abstract override def process(record: ConsumerRecord[K, V]): ProcessCompletionStatus = {

    // TODO Algorithm
    val beforeStatus = beforeProcess(record)
    if (beforeStatus != ProcessCompletionStatuses.Success) return beforeStatus

    val processStatus = super.process(record)
    if (beforeStatus != ProcessCompletionStatuses.Success) return processStatus


    val afterStatus = afterProcess(record)
    return afterStatus


//    if (!beforeProcess(record)) return false
//    if (!super.process(record)) return false
//    if (afterProcess(record)) true else false
  }
}
