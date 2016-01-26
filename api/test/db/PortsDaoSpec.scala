package db

import io.flow.postgresql.Authorization
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class PortsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "validate" must {

    "port > 1024" in {
      Seq(-100, 0, 80, 1024).foreach { port =>
        PortsDao.validate(createPortForm().copy(num = port)) must be(
          Seq("Port must be > 1024")
        )
      }
    }

    "port is unique" in {
      val port = createPort()
      PortsDao.validate(createPortForm().copy(num = port.num)) must be(
        Seq(s"Port ${port.num} is already assigned to the application ${port.applicationId}")
      )
    }

  }

  "create" in {
    val form = createPortForm()
    val port = createPort(form)
    port.num must be(form.num)
  }

  "maxPortNumber" in {
    val port1 = createPort()
    val maxPortNumber = PortsDao.maxPortNumber().getOrElse {
      sys.error("Failed to find max port num")
    }

    val port2 = createPort()
    val newMaxPortNumber = PortsDao.maxPortNumber().getOrElse {
      sys.error("Failed to find max port num")
    }

    newMaxPortNumber > maxPortNumber must be(true)
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

    "filter by nums" in {
      val port1 = createPort()
      val port2 = createPort()

      PortsDao.findAll(Authorization.All, nums = Some(Seq(port1.num, port2.num))).map(_.id).sorted must be(
        Seq(port1.id, port2.id).sorted
      )

      PortsDao.findAll(Authorization.All, nums = Some(Nil)) must be(Nil)
      PortsDao.findAll(Authorization.All, nums = Some(Seq(-5))) must be(Nil)
      PortsDao.findAll(Authorization.All, nums = Some(Seq(port1.num, -10))).map(_.id) must be(Seq(port1.id))
    }

  }

}
