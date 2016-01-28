package io.flow.registry.api.lib

import db.PortsDao
import io.flow.postgresql.Authorization
import io.flow.registry.v0.models.Service
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class DefaultPortAllocatorSpec extends PlaySpec with OneAppPerSuite {

  "offset" in {
    DefaultPortAllocator("", "nodejs").offset must be(Some(0))
    DefaultPortAllocator("", "play").offset must be(Some(1))
    DefaultPortAllocator("foo", "play").offset must be(Some(1))
    DefaultPortAllocator("foo-postgresql", "postgresql").offset must be(Some(9))
    DefaultPortAllocator("foo-bar-postgresql", "postgresql").offset must be(Some(9))
    DefaultPortAllocator("bar", "other").offset must be(None)
  }

  "blacklist block" in {
    DefaultPortAllocator("foo", "play").isBlockAvailable(6000) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(6100) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(6120) must be(true)
    DefaultPortAllocator("foo", "play").isBlockAvailable(7000) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(7080) must be(true)
    DefaultPortAllocator("foo", "play").isBlockAvailable(8000) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(8080) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(8900) must be(false)
    DefaultPortAllocator("foo", "play").isBlockAvailable(8910) must be(true)
  }

  "blacklist ports" in {
    DefaultPortAllocator("foo", "play").isPortAvailable(8080) must be(false)
    DefaultPortAllocator("foo", "play").isPortAvailable(8124) must be(true)
  }

  "toBase" in {
    DefaultPortAllocator("foo", "play").toBase(6100) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6101) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6102) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6103) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6104) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6105) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6106) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6107) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6108) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6109) must be(6100)
    DefaultPortAllocator("foo", "play").toBase(6110) must be(6110)
  }
}
