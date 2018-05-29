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

import com.typesafe.scalalogging.Logger

/**
  *
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class EmbeddedKafka
//(
//                     config: KafkaConfig)
{

//  var kafkaServer: KafkaServerStartable = _


//  def this(zookeeperProps: Properties, kafkaProps: Properties) {
//    this (new KafkaConfig(kafkaProps))
//  }





  def start(): Unit = {
    startZookeeper()
//    startKafka()
  }


  def startZookeeper(): Unit = {
    logger.info("Starting embedded ZooKeeper...")


    logger.info("Embedded ZooKeeper has been started.")
  }

//  def startup(config: KafkaConfig): Unit = {
//    logger.info("Starting embedded Kafka...")
//    kafkaServer = new KafkaServerStartable(config)
//    kafkaServer.startup()
//
//    logger.info("Embedded Kafka has been started.")
//  }
//
//
//  def shutdown(isAwait: Boolean = true): Unit = {
//    kafkaServer.shutdown()
//    kafkaServer.awaitShutdown()
//  }




  def logger = Logger[EmbeddedKafka]

}

//object EmbeddedKafka {
//  def apply(config: KafkaConfig): EmbeddedKafka = new EmbeddedKafka(config)
//}



//public class KafkaLocal {
//
//
//
//
//  public KafkaServerStartable kafka;
//  public ZooKeeperLocal zookeeper;
//
//  public KafkaLocal(Properties kafkaProperties, Properties zkProperties) throws IOException, InterruptedException{
//    KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
//
//    //start local zookeeper
//    System.out.println("starting local zookeeper...");
//    zookeeper = new ZooKeeperLocal(zkProperties);
//    System.out.println("done");
//
//    //start local kafka broker
//    kafka = new KafkaServerStartable(kafkaConfig);
//    System.out.println("starting local kafka broker...");
//    kafka.startup();
//    System.out.println("done");
//  }
//
//
//  public void stop(){
//    //stop kafka broker
//    System.out.println("stopping kafka...");
//    kafka.shutdown();
//    System.out.println("done");
//  }
//
//
//
//}
