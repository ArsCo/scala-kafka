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

package ars.kafka.util

import java.io.{File, FileNotFoundException, IOException}
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

/** Testing utility methods.
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
object TestUtils {

  /**
    * Creates temporary directory. It has strategies to resolve name conflicts.
    *
    * @param prefix the temporary directory name prefix
    *
    * @throws RuntimeException if can't create temporary dir
    *
    * @return the temporary directory
    */
  def makeTemporaryDir(prefix: String): File = {

    val dir = generateRandomNotExistTemporaryDir(prefix)
    if (dir.mkdir()) dir
    else {
      val oneMoreDir = generateRandomNotExistTemporaryDir(prefix)
      if (oneMoreDir.mkdir()) oneMoreDir
      else throw new RuntimeException(s"Can't create temporary dir: ${dir.getAbsolutePath}")
    }
  }


  def systemTemporaryDirPath() = System.getProperty("java.io.tmpdir")

  /**
    * Gets available port.
    *
    * @throws RuntimeException if there's an error occurred
    *
    * @return available port
    */
  def availablePort(): Int = {
    try {
      val socket = new ServerSocket(0)
      try {
        socket.getLocalPort
      } finally {
        socket.close()
      }
    } catch {
      case e: IOException =>
        throw new RuntimeException("Cannot find available port", e);
    }
  }

  @throws[FileNotFoundException]
  def deleteFile(path: File): Boolean = {
    if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath)

    var ret = true
    if (path.isDirectory) {
      for (f <- path.listFiles) {
        ret = ret && deleteFile(f)
      }
    }
    ret && path.delete()
  }

  private def generateRandomTemporaryDir(prefix: String): File = {
    val systemTmpDir = System.getProperty("java.io.tmpdir")
    val randomInt = ThreadLocalRandom.current().nextInt(100000000)
    val dirName = s"$prefix$randomInt"
    new File(systemTmpDir, dirName)
  }

  private def generateRandomNotExistTemporaryDir(prefix: String): File = {
    var d: File = null
    do {
      d = generateRandomTemporaryDir(prefix)
    } while (d.exists())
    d
  }
}
