package db

import io.flow.registry.v0.models.{Application, ApplicationType}
import io.flow.postgresql.Authorization
import java.util.UUID
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

class ApplicationsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  def validatePort(modulus: Int, app: Application) {
    val numbers = app.ports.map(_.number)
    numbers.size must be(1)
    numbers.foreach { n =>
      if (n % 10 != modulus) {
        fail(s"Application of type[${app.`type`}] port[$n] must end in ${modulus}")
      }
    }
  }

  "respects application type when allocating ports" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    validatePort(
      0,
      createApplication(
        createApplicationForm().copy(
          id = base + "-ui",
          `type` = ApplicationType.Ui
        )
      )
    )

    validatePort(
      1,
      createApplication(
        createApplicationForm().copy(
          id = base + "-api",
          `type` = ApplicationType.Api
        )
      )
    )

    validatePort(
      9,
      createApplication(
        createApplicationForm().copy(
          id = base + "-db",
          `type` = ApplicationType.Database
        )
      )
    )


  }

  "allocates ports based on type" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")
    val form = createApplicationForm().copy(`type` = ApplicationType.UNDEFINED("other"))

    val ui = createApplication(createApplicationForm().copy(id = base + "-ui", `type` = ApplicationType.Ui))
    val api = createApplication(createApplicationForm().copy(id = base + "-api", `type` = ApplicationType.Api))
    val postgresql = createApplication(createApplicationForm().copy(id = base + "-db", `type` = ApplicationType.Database))
    val other = createApplication(createApplicationForm().copy(id = base + "-other", `type` = ApplicationType.UNDEFINED("other")))

    val uiPort = ui.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    (uiPort % 10) must be(0)
    api.ports.map(_.number) must be(Seq(uiPort + 1))
    postgresql.ports.map(_.number) must be(Seq(uiPort + 9))
    other.ports.map(_.number) must be(Seq(uiPort + 2))
  }

  "allocates block ranges in sets of 10" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    val api = createApplication(createApplicationForm().copy(id = base))

    // inject the other app here so we have to 'jump' over its allocation
    val other = createApplication(createApplicationForm().copy(id = UUID.randomUUID.toString.replaceAll("\\-", "")))

    val api2 = createApplication(createApplicationForm().copy(id = base + "-api2"))

    val apiPort = api.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val otherPort = other.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port for other app")
    }

    val api2Port = api2.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val offset = otherPort - apiPort + 10
    (offset % 10) must be(0)
    api2Port must be(apiPort + offset)
  }

}
