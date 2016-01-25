package io.flow.registry.api.lib

import io.flow.common.v0.models.Name
import org.specs2.mutable._

class DefaultPortAllocatorSpec extends Specification {

  "offset" in {
    DefaultPortAllocator("", None).offset() must beEqualTo(0)
    DefaultPortAllocator("foo", None).offset() must beEqualTo(0)
    DefaultPortAllocator("foo-postgresql", None).offset() must beEqualTo(9)
    DefaultPortAllocator("foo-bar-postgresql", None).offset() must beEqualTo(9)
    DefaultPortAllocator("  foo-postgresql  ", None).offset() must beEqualTo(9)
  }

  "number" in {
    DefaultPortAllocator("", None).number must beEqualTo(6010)
    DefaultPortAllocator("", Some(6500)).number must beEqualTo(6510)
    DefaultPortAllocator("", Some(6515)).number must beEqualTo(6520)
    DefaultPortAllocator("foo", None).number must beEqualTo(6010)
    DefaultPortAllocator("foo-postgresql", None).number must beEqualTo(6019)
    DefaultPortAllocator("foo-bar-postgresql", None).number must beEqualTo(6019)
    DefaultPortAllocator("  foo-postgresql  ", None).number must beEqualTo(6019)
  }

}
