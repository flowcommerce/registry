package db

import io.flow.postgresql.Authorization
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class ApplicationsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "allocates ports for similarly named applications" in {
    val base = createTestId().replaceAll("\\-", "")

    val api = createApplication(createApplicationForm().copy(id = base))
    val postgresql = createApplication(createApplicationForm().copy(id = base + "-postgresql"))
    val other = createApplication(createApplicationForm().copy(id = base + "-other"))

    val baseNumber = api.ports.map(_.number).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }
    println(s"baseNumber[$baseNumber]")
    (baseNumber % 10) must be(0)
    postgresql.ports.map(_.number) must be(Seq(baseNumber + 9))
    other.ports.map(_.number) must be(Seq(baseNumber + 1))
  }

}
