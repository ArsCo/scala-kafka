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

package ars.kafka.config

import ars.kafka.AbstractBaseTest

/** Tests for [[CommonConfig]]
  *
  * @author Arsen Ibragimov (ars)
  * @since 0.0.1
  */
class CommonConfigTest extends AbstractBaseTest {

  private final val c = new CommonConfig {

    def toMap1(servers: Server*): Map[String, Any] = super.toMap(servers :_*)

    def optionalsToMap1(params: (String, Option[Any])*): Map[String, Any] =
      super.optionalsToMap(params :_*)
  }

  "CommonConfig" must "have correct interface" in {
    assertResult(Map())(c.raw)
    assertResult(c.raw)(c.all)
  }

  "toMap" must "validate args" in {
    intercept[IllegalArgumentException] {
      c.toMap1(null)
    }

    intercept[IllegalArgumentException] {
      c.toMap1(Seq() :_*)
    }

    intercept[IllegalArgumentException] {
      c.toMap1()
    }
  }

  it must "converts sequence of Servers to Map" in {
    val s1 = Server("host1", 1111)
    val s2 = Server("host2", 2222)
    val s3 = Server("host3", 3333)
    val s4 = Server("host4", 4444)

    assertResult(
      Map("bootstrap.servers" -> "host1:1111,host2:2222,host3:3333,host4:4444")
    ) {
      c.toMap1(s1, s2, s3, s4)
    }
  }

  "optionalsToMap" must "validate args" in {
    intercept[IllegalArgumentException] {
      c.optionalsToMap1(null)
    }

    intercept[IllegalArgumentException] {
      c.optionalsToMap1(Seq() :_*)
    }

    intercept[IllegalArgumentException] {
      c.optionalsToMap1((null, None))
    }

    intercept[IllegalArgumentException] {
      c.optionalsToMap1(("", None))
    }

    intercept[IllegalArgumentException] {
      c.optionalsToMap1(("string", null))
    }

    intercept[IllegalArgumentException] {
      c.optionalsToMap1(("string", Some(null)))
    }
  }

  it must "converts sequence of pairs (key, Optional[T]) to Map" in {
    assertResult(
      Map("str2" -> "value", "str3" -> 5)
    ) {
      c.optionalsToMap1(
        ("str1", None),
        ("str2", Some("value")),
        ("str3", Some(5))
      )
    }
  }
}
