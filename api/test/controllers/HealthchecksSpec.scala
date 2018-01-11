package controllers

import io.flow.healthcheck.v0.Client
import io.flow.healthcheck.v0.models.Healthcheck
import play.api.test._
import util.FlowPlaySpec

class HealthchecksSpec extends FlowPlaySpec {

  import scala.concurrent.ExecutionContext.Implicits.global

  override lazy val port = 9010
  lazy val client = new Client(wsClient, s"http://localhost:$port")

  "GET /_internal_/healthcheck" in new WithServer(port=port) {
    await(
      client.Healthchecks.getHealthcheck()
    ) must be (
      Healthcheck("healthy")
    )
  }

}
