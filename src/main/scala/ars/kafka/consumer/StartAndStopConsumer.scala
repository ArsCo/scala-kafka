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

import scala.util.Try

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
trait StartAndStopConsumer[Key, Value] extends AbstractCommonSingleThreadConsumer[Key, Value] {

  def interceptStart(): Unit

  def interceptStop(): Unit

  override protected def startConsummingLoop(): Unit = {
    Try(interceptStart())
    super.startConsummingLoop()
    Try(interceptStop())
  }
}




//object O {
//  def aaa(): Unit = {
//    new AbstractSingleThreadConsumer[String, String] with StartAndStopConsumer[String, String] {
//
//      val stopList: Seq[ddd]
//        va;
//      override def interceptStart(): Unit = {
//        startList.foreach(_.)
//      }
//
//      override def interceptStop(): Unit = {
//        stopList.foreach(_.)
//      }
//
//
//      addStartListener() {
//
//      }
//
//    }
//  }
//}





//trait StartAndStopListenerConsumer[Key, Value] extends StartAndStopConsumer[Key, Value] {
//
//  def addStartListener(): Unit = {
//
//  }
//
//  def add
//}
