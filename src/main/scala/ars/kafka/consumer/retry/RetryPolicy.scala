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

package ars.kafka.consumer.retry

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait RetryPolicy {



}

/**
  * Default retry policy.
  *
  *
  *
  */
//class ForwardRetryPolicy {
//
//  private[this] var uncommited:
//}


// not added
// skipped few
// completed





//class FlippedBuffer(offset: Int = 0, maxSize: Int = 10000) {
//
//  abstract sealed class State(value: Int)
//  object States {
//    final case object NotAdded extends State(-1)
//    final case class Skipped(count: Int) extends State(1)
//    final case object Failure extends State(2)
//    final case object Success extends State(3)
//  }
//
//
//  private var firstElementOffset = 0
//  private var buffer = List.fill(maxSize)(States.NotAdded)
//
//
//  def skip(offset: Long): Unit = {
//    if (offset > firstElementOffset + buffer.size)
//  }
//
//  def success(offset: Long): Unit = {
//
//  }
//
//  def failure(offset: Long): Unit = {
//
//  }
//
//  def currentStatus(offset: Long): State = {
//
//  }
//
//  def hasUncommited(): Boolean = {
//
//  }
//
//  def offsetToCommit(): Long = {
//
//  }
//
//  private[this] def shift(): Unit = {
//
//  }

//  def addNew(offset: Int): Unit = {
//    if (offset < firstElementOffset + buffer.size) {
//
//    } else {
//      buffer
//    }
//  }

//  def add(offset: Int, value: Int): Unit = {
//
//  }



//}
