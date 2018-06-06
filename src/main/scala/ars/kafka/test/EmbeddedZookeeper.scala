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

import java.io.{File, FileNotFoundException, IOException}
import java.net.InetSocketAddress
import java.util.concurrent.ThreadLocalRandom

import ars.kafka.test.EmbeddedZookeeper._
import ars.kafka.util.TestUtils.{availablePort, deleteFile, systemTemporaryDirPath}
import ars.precondition.require.Require.Default._
import com.typesafe.scalalogging.Logger
import org.apache.zookeeper.server.{NIOServerCnxnFactory, ServerCnxnFactory, ZooKeeperServer}

/** Embedded Zookeeper instance. To start ZooKeeper create new instance and call `startup()`
  *
  * @param port the server listening port (must be non-null).
  *             If `None` then port will be selected automatically.
  * @param instanceId the server instance identifier (must be non-blank).
  *                   If not set then instance identifier will be selected automatically.
  * @param baseDirName the base data dir (must be non-blank).
  * @param snapshotDirPrefix the snapshot directory prefix (must be non-blank)
  * @param logDirPrefix the transaction log directory prefix (must be non-blank)
  *
  * @throws IllegalArgumentException if any arg is not valid
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class EmbeddedZookeeper(
                         port: Option[Int] = None,
                         val instanceId: String = ThreadLocalRandom.current().nextInt().toString,
                         val baseDirName: String = DefaultBaseDirName,
                         val snapshotDirPrefix: String = DefaultSnapshotDirPrefix,
                         val logDirPrefix: String = DefaultLogDirPrefix
                       ) {
  requireNotNull(port, "port")
  requireNotBlank(instanceId, "instanceId")
  requireNotBlank(baseDirName, "baseDirName")
  requireNotBlank(snapshotDirPrefix, "snapshotDirName")
  requireNotBlank(logDirPrefix, "logDirName")

  private[this] val snapshot = tmpInstanceDir(logDirPrefix)
  private[this] val log = tmpInstanceDir(snapshotDirPrefix)

  private[this] var factory: ServerCnxnFactory = _
  private[this] var isStarted = false

  /**
    * The server listening port.
    */
  lazy val serverPort: Int = port.getOrElse(availablePort())

  /**
    * @return `true` if server is running and `false` otherwise
    */
  def isRunning: Boolean = isStarted

  /**
    * @throws IllegalStateException if calls when server is not running
    *
    * @return the snapshot directory.
    */
  def snapshotDir: File = {
    if (isStarted) snapshot
    else throw new IllegalStateException("There's no snapshot directory when server is not running.")
  }

  /**
    * @throws IllegalStateException if calls when server is not running
    *
    * @return the transaction log directory
    */
  def logDir: File = {
    if (isStarted) log
    else throw new IllegalStateException("There's no transaction log directory when server is not running.")
  }

  /**
    * Starts up server.
    *
    * @throws IllegalArgumentException if any arg is not valid
    * @throws IllegalStateException if calls for already running server
    * @throws RuntimeException if there's a problem occur
    *
    * @param tickTime the tick time in milliseconds (must be positive)
    * @param maxConnections the max connections (must be positive)
    */
  def startup(tickTime: Int = 500, maxConnections: Int = 1024): Unit = {
    requirePositive(tickTime, "tickTime")
    requirePositive(maxConnections, "maxConnections")

    if (isStarted) throw new IllegalStateException("Server already running.")

    logger.info("Starting embedded zookeeper server...")
    logger.info(s"Zookeeper snapshot directory: ${snapshot.getAbsolutePath}")
    logger.info(s"Zookeeper logging directory: ${log.getAbsolutePath}")
    logger.info(s"Zookeeper port: $serverPort")

    try {
      factory = createServerFactory(maxConnections)
      val server = new ZooKeeperServer(snapshot, log, tickTime)
      factory.startup(server)
      isStarted = true
    } catch {
      case e: InterruptedException => throw new RuntimeException(e)
    }

    logger.info("Embedded zookeeper server have started.")
  }

  /**
    * Shuts down server.
    *
    * @throws IllegalStateException if calls for already stopped server
    * @throws RuntimeException if there's a problem occur
    */
  def shutdown(): Unit ={
    if (!isStarted) throw new IllegalStateException("Server already was shutted down.")

    logger.info("Shutting down embedded zookeeper server...")

    factory.shutdown()
    isStarted = false
    try {
      deleteFile(snapshot)
    } catch {
      case _: FileNotFoundException => // ignore
      case e: IOException =>
        logger.error(s"Can't remove snapshot directory: ${snapshot.getAbsolutePath}.", e)
      case e =>
        logger.error(s"Error while removing snapshot directory: ${snapshot.getAbsolutePath}.", e)
    }

    try {
      deleteFile(log)
    } catch {
      case _: FileNotFoundException => // ignore
      case e: IOException =>
        logger.error(s"Can't remove transaction log directory: ${log.getAbsolutePath}.", e)
      case e =>
        logger.error(s"Error while removing transaction log directory: ${log.getAbsolutePath}.", e)
    }

    logger.info("Embedded zookeeper server have shutted down.")
  }

  private[this] def tmpDir(path: String): File = new File(systemTemporaryDirPath(), path)
  private[this] def tmpInstanceDir(prefix: String): File = tmpDir(s"$baseDirName/${prefix}_$instanceId")

  private def createServerFactory(maxConnections: Int): NIOServerCnxnFactory = {
    val factory = new NIOServerCnxnFactory()
    factory.configure(new InetSocketAddress("localhost", serverPort), maxConnections)
    factory
  }

  private def logger = Logger[EmbeddedZookeeper]
}

object EmbeddedZookeeper {
  final val DefaultBaseDirName = "zookeeper"
  final val DefaultSnapshotDirPrefix = "snapshot"
  final val DefaultLogDirPrefix = "log"
}