package controllers

import io.flow.registry.v0.Client
import io.flow.registry.v0.models.Healthcheck

import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

class HealthchecksSpec extends PlaySpec with OneServerPerSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit override lazy val port = 9010
  implicit override lazy val app: FakeApplication = FakeApplication()

  lazy val client = new Client(s"http://localhost:$port")

  "GET /_internal_/healthcheck" in new WithServer {
    await(
      client.healthchecks.getInternalAndHealthcheck()
    ) must beEqualTo(
      Some(Healthcheck("healthy"))
    )
  }

}
