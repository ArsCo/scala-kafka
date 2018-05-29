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

package ars.kafka.test

import java.io.File
import java.util.Properties

import ars.kafka.util.TestUtils
import kafka.server.{KafkaConfig, KafkaServer}
import org.apache.kafka.common.utils.SystemTime

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class EmbeddedKafkaCluster(zk: String, props: Properties, number: Int, ports: Int*) {

  private final val LogDirName = "kafka-local"

  private var brokers = Seq[KafkaServer]()
  private var logDirs = Seq[File]()

  private def getPort(i: Int): Int = {
    if (i < ports.size) ports(i)
    else TestUtils.availablePort()
  }

  private def createProps(props: Properties, zk: String, brokerId: Int, port: Int, logDir: File): Properties = {
    val p = new Properties()
    p.putAll(props)
    p.setProperty("zookeeper.connect", zk)
    p.setProperty("broker.id", brokerId.toString)
    p.setProperty("host.name", "localhost")
    p.setProperty("port", port.toString)
    p.setProperty("log.dir", logDir.getAbsolutePath)
    p.setProperty("log.flush.interval.messages", 1.toString)
    p
  }

  def startup(): Unit = {
    for (i <- 0 until number) {
      val port = getPort(i)
      val logDir = TestUtils.makeTemporaryDir(LogDirName)

      val props = createProps(new Properties(), zk, i+1, port, logDir)
      val broker = startBroker(props)

      brokers :+= broker
      logDirs :+= logDir
    }
  }

  private def createConfig(props: Properties): KafkaConfig = {
    new KafkaConfig(props)
  }

  private def startBroker(props: Properties): KafkaServer  = {
    val server = new KafkaServer(new KafkaConfig(props), new SystemTime())
    server.startup()
    server
  }




//    for (int i = 0; i < ports.size(); i++) {
//      Integer port = ports.get(i);
//      File logDir = TestUtils.constructTempDir("kafka-local");
//
//      Properties properties = new Properties();
//      properties.putAll(baseProperties);
//      properties.setProperty("zookeeper.connect", zkConnection);
//      properties.setProperty("broker.id", String.valueOf(i + 1));
//      properties.setProperty("host.name", "localhost");
//      properties.setProperty("port", Integer.toString(port));
//      properties.setProperty("log.dir", logDir.getAbsolutePath());
//      properties.setProperty("log.flush.interval.messages", String.valueOf(1));
//
//      KafkaServer broker = startBroker(properties);
//
//      brokers.add(broker);
//      logDirs.add(logDir);
//
//  }

//  def shutdown(): Unit = {
//    for (KafkaServer broker : brokers) {
//      try {
//        broker.shutdown();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//    for (File logDir : logDirs) {
//      try {
//        TestUtils.deleteFile(logDir);
//      } catch (FileNotFoundException e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
//
//  private def startBroker(props: Properties): KafkaServer  = {
//    server = new KafkaServer(new KafkaConfig(props), new SystemTime())
//    server.startup()
//    server
//  }

}
