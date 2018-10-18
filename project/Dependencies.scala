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

  val LogbackVersion = "1.2.3"
  val LogstashEncoderVersion = "5.0"

  val KafkaVersion = "1.1.0"
  val ZooKeeperVersion = "3.4.11"

  val ScalaPreconditionsVersion = "0.1.2"
  val ScalaCommonVersion = "0.0.4"

  val ScalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion

  val LoggingImpl = Seq(
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
    "net.logstash.logback" % "logstash-logback-encoder" % LogstashEncoderVersion
  )

  val ZooKeeper = "org.apache.zookeeper" % "zookeeper" % ZooKeeperVersion

  val KafkaClient = "org.apache.kafka" % "kafka-clients" % KafkaVersion
//  val KafkaServer = "org.apache.kafka" %% "kafka" % KafkaVersion // TODO remove

  val ScalaTest = "org.scalatest" %% "scalatest" % ScalaTestVersion % Test

  val ScalaPreconditions = "ru.ars-co" %% "scala-preconditions" % ScalaPreconditionsVersion
  val ScalaCommon = "ru.ars-co" %% "scala-common" % ScalaCommonVersion

  val scala = Seq(ScalaPreconditions, ScalaCommon)
  val logging = Seq(ScalaLogging)

  val zookeeper = Seq(ZooKeeper)
  val kafka = Seq(KafkaClient)

  val testing = Seq(ScalaTest) ++ LoggingImpl.map(_ % Test)
}
