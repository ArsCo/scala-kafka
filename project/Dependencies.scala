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

import sbt._

/** `build.sbt` dependencies.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object Dependencies {
  val ScalaLoggingVersion = "3.7.2"
  val ScalaTestVersion = "3.0.0"

  val KafkaVersion = "1.1.0"
  val ZooKeeperVersion = "3.4.11"

  val ScalaPreconditionsVersion = "0.1.0"

  val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion
  val Jcl2slf = "org.slf4j" % "jcl-over-slf4j" % "1.7.21"

  val ScalaTest = "org.scalatest" %% "scalatest" % ScalaTestVersion % Test

  val KafkaClient = "org.apache.kafka" % "kafka-clients" % KafkaVersion
  val KafkaServer = "org.apache.kafka" %% "kafka" % KafkaVersion

  val ZooKeeper = "org.apache.zookeeper" % "zookeeper" % ZooKeeperVersion

  val ScalaPreconditions = "ru.ars-co" %% "scala-preconditions" % ScalaPreconditionsVersion


  val logging = Seq(ScalaLogging, Jcl2slf)
  val testing = Seq(ScalaTest)
  val kafka = Seq(KafkaClient, KafkaServer)
  val zookeeper = Seq(ZooKeeper)
  val preconditions = Seq(ScalaPreconditions)

}
