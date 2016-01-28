package db

import io.flow.registry.v0.models.{Application, ApplicationPutForm, Service}
import io.flow.postgresql.Authorization
import java.util.UUID
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

class ApplicationsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  def validatePort(modulus: Int, app: Application) {
    app.ports.size must be(1)
    app.ports.foreach { p =>
      if (p.external % 10 != modulus) {
        fail(s"Application port[${p.external}] for service[${p.service.id}] must end in ${modulus}")
      }
    }
  }

  "respects application service when allocating ports" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    validatePort(
      0,
      createApplication(
        createApplicationForm().copy(
          id = base + "-ui",
          service = "nodejs"
        )
      )
    )

    validatePort(
      1,
      createApplication(
        createApplicationForm().copy(
          id = base + "-api",
          service = "play"
        )
      )
    )

    validatePort(
      9,
      createApplication(
        createApplicationForm().copy(
          id = base + "-db",
          service = "postgresql"
        )
      )
    )


  }

  "allocates ports based on type" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    val ui = createApplication(createApplicationForm().copy(id = base + "-ui", service = "nodejs"))
    val api = createApplication(createApplicationForm().copy(id = base + "-api", service = "play"))
    val postgresql = createApplication(createApplicationForm().copy(id = base + "-db", service = "postgresql"))

    val uiPort = ui.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    (uiPort % 10) must be(0)
    api.ports.map(_.external) must be(Seq(uiPort + 1))
    postgresql.ports.map(_.external) must be(Seq(uiPort + 9))
  }

  "allocates block ranges in sets of 10" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    val api = createApplication(createApplicationForm().copy(id = base, service = "play"))

    // inject the other app here so we have to 'jump' over its allocation
    val other = createApplication(createApplicationForm().copy(id = createUrlKey(), service = "play"))

    val api2 = createApplication(createApplicationForm().copy(id = base + "-api2", service = "play"))

    val apiPort = api.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val otherPort = other.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port for other app")
    }

    val api2Port = api2.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val offset = otherPort - apiPort + 10
    (offset % 10) must be(0)

    Seq(apiPort + offset, apiPort + offset + 10).contains(api2Port) must be(true)
  }

  "deleting an application also deletes its ports" in {
    val app = createApplication()
    val portNumber = app.ports.map(_.external).headOption.getOrElse {
      sys.error("No port for application")
    }
    PortsDao.findByExternal(Authorization.All, portNumber).getOrElse {
      sys.error("Could not find port")
    }

    ApplicationsDao.delete(testUser, app)
    ApplicationsDao.findById(Authorization.All, app.id) must be(None)
    PortsDao.findByExternal(Authorization.All, portNumber) must be(None)
  }

  "upsert creates new application" in {
    val id = createApplicationForm().id
    val app = rightOrErrors(
      ApplicationsDao.upsert(testUser, id, ApplicationPutForm(service = "play"))
    )

    app.id must be(id)
  }

  "upsert allocates new ports" in {
    val app = createApplication(createApplicationForm().copy(service = "nodejs"))
    val portNumber = app.ports.map(_.external).headOption.getOrElse {
      sys.error("No port for application")
    }
    app.ports.map(_.external) must be(Seq(portNumber))

    val updated = rightOrErrors(
      ApplicationsDao.upsert(
        testUser,
        app.id,
        ApplicationPutForm(service = "play")
      )
    )
    updated.ports.map(_.external) must be(Seq(portNumber, portNumber + 1))

    // Now test idempotence
    val updatedAgain = rightOrErrors(
      ApplicationsDao.upsert(
        testUser,
        app.id,
        ApplicationPutForm(service = "play")
      )
    )
    updatedAgain.ports.map(_.external) must be(Seq(portNumber, portNumber + 1))
  }

  "can reuse ID once deleted" in {
    val app = createApplication()
    ApplicationsDao.delete(testUser, app)
    val app2 = createApplication(createApplicationForm().copy(id = app.id))
  }

}
