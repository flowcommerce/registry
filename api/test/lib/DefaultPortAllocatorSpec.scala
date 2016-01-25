package io.flow.registry.api.lib

import io.flow.postgresql.Authorization
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class DefaultPortAllocatorSpec extends PlaySpec with OneAppPerSuite {

  "offset" in {
    DefaultPortAllocator("", None).offset must be(None)
    DefaultPortAllocator("foo", None).offset must be(None)
    DefaultPortAllocator("foo-postgresql", None).offset must be(Some(9))
    DefaultPortAllocator("foo-bar-postgresql", None).offset must be(Some(9))
    DefaultPortAllocator("  foo-postgresql  ", None).offset must be(Some(9))
  }

  "number" in {
    DefaultPortAllocator("", None).number must be(6010)
    DefaultPortAllocator("", Some(6500)).number must be(6510)
    DefaultPortAllocator("", Some(6515)).number must be(6520)
    DefaultPortAllocator("foo", None).number must be(6010)
    DefaultPortAllocator("foo-postgresql", None).number must be(6019)
    DefaultPortAllocator("foo-bar-postgresql", None).number must be(6019)
    DefaultPortAllocator("  foo-postgresql  ", None).number must be(6019)
  }

  "blacklist" in {
    DefaultPortAllocator("foo", Some(8070)).number must be(8090)
  }

}
