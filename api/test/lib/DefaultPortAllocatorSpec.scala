package io.flow.registry.api.lib

import db.PortsDao
import io.flow.postgresql.Authorization
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class DefaultPortAllocatorSpec extends PlaySpec with OneAppPerSuite {

  "offset" in {
    DefaultPortAllocator("").offset must be(None)
    DefaultPortAllocator("foo").offset must be(None)
    DefaultPortAllocator("foo-postgresql").offset must be(Some(9))
    DefaultPortAllocator("foo-bar-postgresql").offset must be(Some(9))
    DefaultPortAllocator("  foo-postgresql  ").offset must be(Some(9))
  }

  "blacklist block" in {
    DefaultPortAllocator("foo").isBlockAvailable(6000) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(6100) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(6120) must be(true)
    DefaultPortAllocator("foo").isBlockAvailable(7000) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(7080) must be(true)
    DefaultPortAllocator("foo").isBlockAvailable(8000) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(8080) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(8900) must be(false)
    DefaultPortAllocator("foo").isBlockAvailable(8910) must be(true)
  }

  "blacklist ports" in {
    DefaultPortAllocator("foo").isPortAvailable(8080) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(8124) must be(true)
  }

  "toBase" in {
    DefaultPortAllocator("foo").toBase(6100) must be(6100)
    DefaultPortAllocator("foo").toBase(6101) must be(6100)
    DefaultPortAllocator("foo").toBase(6102) must be(6100)
    DefaultPortAllocator("foo").toBase(6103) must be(6100)
    DefaultPortAllocator("foo").toBase(6104) must be(6100)
    DefaultPortAllocator("foo").toBase(6105) must be(6100)
    DefaultPortAllocator("foo").toBase(6106) must be(6100)
    DefaultPortAllocator("foo").toBase(6107) must be(6100)
    DefaultPortAllocator("foo").toBase(6108) must be(6100)
    DefaultPortAllocator("foo").toBase(6109) must be(6100)
    DefaultPortAllocator("foo").toBase(6110) must be(6110)
  }
}
