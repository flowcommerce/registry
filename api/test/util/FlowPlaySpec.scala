package util

import java.util.UUID

import io.flow.common.v0.models.UserReference
import io.flow.play.util.{AuthHeaders, IdGenerator}
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.reflect.ClassTag

//TODO move to lib-play
trait FlowPlaySpec extends PlaySpec with GuiceOneServerPerSuite with FutureAwaits with DefaultAwaitTimeout with MustMatchers {

  def init[T: ClassTag]: T = {
    app.injector.instanceOf[T]
  }

  lazy val wsClient: WSClient = init[WSClient]
  lazy val authHeaders: AuthHeaders = init[AuthHeaders]
  lazy val testHeaders: Seq[(String, String)] = authHeaders.headers(AuthHeaders.user(testUser))
  lazy val testUser: UserReference = {
    UserReference(id = idGenerator.randomId())
  }
  private[this] lazy val idGenerator = IdGenerator("tst")

  def createTestId(): String = {
    idGenerator.randomId()
  }

  def createUrlKey(prefix: String = "tst"): String = {
    prefix + UUID.randomUUID.toString.replaceAll("\\-", "")
  }

  def createTestName(): String = {
    s"Z Test ${UUID.randomUUID}"
  }

  def createTestEmail(): String = {
    createTestId + "@test.flow.io"
  }

  def rightOrErrors[T](result: Either[Seq[String], T]): T = {
    result match {
      case Left(errors) => sys.error(errors.mkString(", "))
      case Right(obj) =>
        obj
    }
  }

}