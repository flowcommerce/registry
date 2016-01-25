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
        PortsDao.validate(createPortForm().copy(number = port)) must be(
          Seq("Port must be > 1024")
        )
      }
    }

    "port is unique" in {
      val port = createPort()
      PortsDao.validate(createPortForm().copy(number = port.number)) must be(
        Seq(s"Port ${port.number} is already assigned to the application ${port.applicationId}")
      )
    }

  }

  "create" in {
    val form = createPortForm()
    val port = createPort(form)
    port.number must be(form.number)
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

    "filter by numbers" in {
      val port1 = createPort()
      val port2 = createPort()

      PortsDao.findAll(Authorization.All, numbers = Some(Seq(port1.number, port2.number))).map(_.id).sorted must be(
        Seq(port1.id, port2.id).sorted
      )

      PortsDao.findAll(Authorization.All, numbers = Some(Nil)) must be(Nil)
      PortsDao.findAll(Authorization.All, numbers = Some(Seq(-5))) must be(Nil)
      PortsDao.findAll(Authorization.All, numbers = Some(Seq(port1.number, -10))).map(_.id) must be(Seq(port1.id))
    }

  }

}
