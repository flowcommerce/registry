package db

import io.flow.postgresql.Authorization
import java.util.UUID
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

class ApplicationsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "allocates ports for similarly named applications" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    val api = createApplication(createApplicationForm().copy(id = base))
    val postgresql = createApplication(createApplicationForm().copy(id = base + "-postgresql"))
    val other = createApplication(createApplicationForm().copy(id = base + "-other"))

    val apiPort = api.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    (apiPort % 10) must be(0)
    postgresql.ports.map(_.number) must be(Seq(apiPort + 9))
    other.ports.map(_.number) must be(Seq(apiPort + 1))
  }

  "allocates block ranges in sets of 10" in {
    val base = UUID.randomUUID.toString.replaceAll("\\-", "")

    val api = createApplication(createApplicationForm().copy(id = base))

    // inject the other app here so we have to 'jump' over its allocation
    val other = createApplication(createApplicationForm().copy(id = UUID.randomUUID.toString.replaceAll("\\-", "")))

    val additionalApplications = 1.until(11) map { i =>
      createApplication(createApplicationForm().copy(id = base + s"-app$i"))
    }

    val apiPort = api.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val otherPort = other.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port for other app")
    }

    val offset = otherPort - apiPort + 10
    (offset % 10) must be(0)

    additionalApplications.flatMap(_.ports).map(_.number) must be(
      Seq(
        apiPort + 1, apiPort + 2, apiPort + 3, apiPort + 4, apiPort + 5, apiPort + 6,
        apiPort + 7, apiPort + 8, apiPort + offset, apiPort + offset + 1
      )
    )
  }

}
