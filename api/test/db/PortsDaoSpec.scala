package db

import io.flow.postgresql.Authorization
import util.RegistrySpec

class PortsDaoSpec extends RegistrySpec {

  "create" in {
    val form = createPortForm()
    val port: InternalPort = createPort(form)
    port.external must be(form.external)
  }

  "maxExternalPortNumber" in {
    createPort()
    val maxExternalPortNumber = portsDao.maxExternalPortNumber().getOrElse {
      sys.error("Failed to find max port external")
    }

    createPort()
    val newMaxPortNumber = portsDao.maxExternalPortNumber().getOrElse {
      sys.error("Failed to find max external port number")
    }

    newMaxPortNumber > maxExternalPortNumber must be(true)
  }

  "findById" in {
    val port = createPort()
    portsDao.findById(Authorization.All, port.id).map(_.id) must be(
      Some(port.id),
    )

    portsDao.findById(Authorization.All, createTestId()) must be(None)
  }

  "findAll" must {

    "filter by ids" in {
      val port1 = createPort()
      val port2 = createPort()

      portsDao.findAll(Authorization.All, ids = Some(Seq(port1.id, port2.id))).map(_.id).sorted must be(
        Seq(port1.id, port2.id).sorted,
      )

      portsDao.findAll(Authorization.All, ids = Some(Nil)) must be(Nil)
      portsDao.findAll(Authorization.All, ids = Some(Seq(createTestId()))) must be(Nil)
      portsDao.findAll(Authorization.All, ids = Some(Seq(port1.id, createTestId()))).map(_.id) must be(Seq(port1.id))
    }

    "filter by externals" in {
      val port1 = createPort()
      val port2 = createPort()

      portsDao
        .findAll(Authorization.All, externals = Some(Seq(port1.external, port2.external)))
        .map(_.id)
        .sorted must be(
        Seq(port1.id, port2.id).sorted,
      )

      portsDao.findAll(Authorization.All, externals = Some(Nil)) must be(Nil)
      portsDao.findAll(Authorization.All, externals = Some(Seq(-5))) must be(Nil)
      portsDao.findAll(Authorization.All, externals = Some(Seq(port1.external, -10))).map(_.id) must be(Seq(port1.id))
    }

  }

}
