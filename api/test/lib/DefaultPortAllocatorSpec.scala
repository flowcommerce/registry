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

  "number" in {
    val nextPort: Long = PortsDao.maxPortNumber() match {
      case None => 6000
      case Some(max) => max + 10
    }

    DefaultPortAllocator("").number must be(nextPort)
    DefaultPortAllocator("").number must be(nextPort)
    DefaultPortAllocator("").number must be(nextPort)
    DefaultPortAllocator("foo").number must be(nextPort)
    DefaultPortAllocator("foo-postgresql").number must be(nextPort + 9)
    DefaultPortAllocator("foo-bar-postgresql").number must be(nextPort + 9)
    DefaultPortAllocator("  foo-postgresql  ").number must be(nextPort + 9)
  }

  "blacklist" in {
    DefaultPortAllocator("foo").isPortAvailable(8080) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(6000) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(6100) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(7000) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(8000) must be(false)
    DefaultPortAllocator("foo").isPortAvailable(8900) must be(false)
  }

}
