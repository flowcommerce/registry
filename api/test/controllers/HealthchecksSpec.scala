package controllers

import io.flow.healthcheck.v0.Client
import io.flow.healthcheck.v0.models.Healthcheck
import io.flow.test.utils.FlowPlaySpec
import org.scalatestplus.play.PortNumber
import play.api.test._

class HealthchecksSpec extends FlowPlaySpec {

  import scala.concurrent.ExecutionContext.Implicits.global

  override lazy val portNumber: PortNumber = PortNumber(9010)
  lazy val client = new Client(wsClient, s"http://localhost:$port")

  "GET /_internal_/healthcheck" in new WithServer(port = port) {
    await(
      client.Healthchecks.getHealthcheck(),
    ) must be(
      Healthcheck("healthy"),
    )
  }

}
