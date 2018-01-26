package io.flow.registry.api.lib

import util.RegistrySpec

class DefaultPortAllocatorSpec extends RegistrySpec {
  
  "offset" in {
    defaultPortAllocator.offset("nodejs") must be(Some(0))
    defaultPortAllocator.offset("play") must be(Some(1))
    defaultPortAllocator.offset("play") must be(Some(1))
    defaultPortAllocator.offset("postgresql") must be(Some(9))
    defaultPortAllocator.offset("postgresql") must be(Some(9))
    defaultPortAllocator.offset("other") must be(None)
  }

  "blacklist block" in {
    defaultPortAllocator.isBlockAvailable(6000) must be(false)
    defaultPortAllocator.isBlockAvailable(6100) must be(false)
    defaultPortAllocator.isBlockAvailable(6120) must be(true)
    defaultPortAllocator.isBlockAvailable(7000) must be(false)
    defaultPortAllocator.isBlockAvailable(7080) must be(true)
    defaultPortAllocator.isBlockAvailable(8000) must be(false)
    defaultPortAllocator.isBlockAvailable(8080) must be(false)
    defaultPortAllocator.isBlockAvailable(8900) must be(false)
    defaultPortAllocator.isBlockAvailable(8910) must be(true)
  }

  "blacklist ports" in {
    defaultPortAllocator.isPortAvailable(8080) must be(false)
    defaultPortAllocator.isPortAvailable(8124) must be(true)
  }

  "toBase" in {
    defaultPortAllocator.toBase(6100) must be(6100)
    defaultPortAllocator.toBase(6101) must be(6100)
    defaultPortAllocator.toBase(6102) must be(6100)
    defaultPortAllocator.toBase(6103) must be(6100)
    defaultPortAllocator.toBase(6104) must be(6100)
    defaultPortAllocator.toBase(6105) must be(6100)
    defaultPortAllocator.toBase(6106) must be(6100)
    defaultPortAllocator.toBase(6107) must be(6100)
    defaultPortAllocator.toBase(6108) must be(6100)
    defaultPortAllocator.toBase(6109) must be(6100)
    defaultPortAllocator.toBase(6110) must be(6110)
  }
}
