package controllers

import db.Helpers
import io.flow.play.util.AuthData
import io.flow.test.utils.FlowPlaySpec
import io.flow.usage.util.UsageUtil
import io.flow.usage.v0.Client
import io.flow.usage.v0.models.json._
import org.scalatest.BeforeAndAfter
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration._

class UsageSpec extends FlowPlaySpec {
  def uu = app.injector.instanceOf[UsageUtil]

  import scala.concurrent.ExecutionContext.Implicits.global

  "Check usage"  in {
    val j = Json.toJson(uu.currentUsage)
    println(s"Found API Usage: $j" )
    val r = Json.toJson(
      Await.result(
        new Client(
          wsClient,
          s"http://localhost:$port",
          defaultHeaders = authHeaders.headers(AuthData.Anonymous.Empty)
        ).Usages.getUsage(), 3 seconds
      )
    )

    j must be(r)
  }
}

