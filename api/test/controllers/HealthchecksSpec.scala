package controllers

import io.flow.common.v0.models.Healthcheck
import io.flow.registry.v0.Client

import play.api.libs.ws._
import play.api.test._

class HealthchecksSpec extends PlaySpecification {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val port = 9010
  lazy val client = new Client(s"http://localhost:$port")

  "GET /_internal_/healthcheck" in new WithServer(port=port) {
    await(
      client.Healthchecks.getHealthcheck()
    ) must beEqualTo(
      Healthcheck("healthy")
    )
  }

}
