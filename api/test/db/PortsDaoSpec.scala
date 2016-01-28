package db

import io.flow.postgresql.Authorization
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class PortsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "create" in {
    val form = createPortForm()
    val port = createPort(form)
    port.external must be(form.external)
  }

  "maxExternalPortNumber" in {
    val port1 = createPort()
    val maxExternalPortNumber = PortsDao.maxExternalPortNumber().getOrElse {
      sys.error("Failed to find max port external")
    }

    val port2 = createPort()
    val newMaxPortNumber = PortsDao.maxExternalPortNumber().getOrElse {
      sys.error("Failed to find max external port number")
    }

    newMaxPortNumber > maxExternalPortNumber must be(true)
  }

  "findById" in {
    val port = createPort()
    PortsDao.findById(Authorization.All, port.id).map(_.id) must be(
      Some(port.id)
    )

    PortsDao.findById(Authorization.All, createTestId()) must be(None)
  }

  "findAll" must {

    "filter by ids" in {
      val port1 = createPort()
      val port2 = createPort()

      PortsDao.findAll(Authorization.All, ids = Some(Seq(port1.id, port2.id))).map(_.id).sorted must be(
        Seq(port1.id, port2.id).sorted
      )

      PortsDao.findAll(Authorization.All, ids = Some(Nil)) must be(Nil)
      PortsDao.findAll(Authorization.All, ids = Some(Seq(createTestId()))) must be(Nil)
      PortsDao.findAll(Authorization.All, ids = Some(Seq(port1.id, createTestId()))).map(_.id) must be(Seq(port1.id))
    }

    "filter by externals" in {
      val port1 = createPort()
      val port2 = createPort()

      PortsDao.findAll(Authorization.All, externals = Some(Seq(port1.external, port2.external))).map(_.id).sorted must be(
        Seq(port1.id, port2.id).sorted
      )

      PortsDao.findAll(Authorization.All, externals = Some(Nil)) must be(Nil)
      PortsDao.findAll(Authorization.All, externals = Some(Seq(-5))) must be(Nil)
      PortsDao.findAll(Authorization.All, externals = Some(Seq(port1.external, -10))).map(_.id) must be(Seq(port1.id))
    }

  }

}
