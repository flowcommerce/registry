package controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.common.v0.models.UserReference
import io.flow.play.clients.{MockConfig, MockTokenClient}
import io.flow.play.util.Config
import io.flow.token.v0.models.Token
import io.flow.registry.v0.errors.{ErrorsResponse, UnitResponse}
import io.flow.registry.v0.{Authorization, Client}
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait MockClient extends db.Helpers {

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)

  val port = 9010

  lazy val anonClient = new Client(s"http://localhost:$port")

  private[this] lazy val mockConfig = play.api.Play.current.injector.instanceOf[Config].asInstanceOf[MockConfig]

  def identifiedClient(
    user: UserReference = testUser,
    token: String = createTestId()
  ): Client = {
    val mockClient = play.api.Play.current.injector.instanceOf[MockTokenClient]
    mockClient.data.add(token, Token(id = createTestId, user = user, createdAt = new DateTime))

    new Client(
      s"http://localhost:$port",
      auth = Some(Authorization.Basic(mockClient.tokens.data.tokens.head.key))
    )
  }

  def jwtClient(user: UserReference = testUser): Client = {
    val header = "Authorization" -> createJWTHeader(user.id)

    new Client(
      s"http://localhost:$port",
      defaultHeaders = Seq(header)
    )
  }

  def createJWTHeader(userId: String, salt: String = mockConfig.requiredString("JWT_SALT")): String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(Map("id" -> userId))
    val token = JsonWebToken(header, claimsSet, salt)
    s"Bearer $token"
  }

  def expectErrors[T](
    f: => Future[T],
    duration: Duration = DefaultDuration
  ): ErrorsResponse = {
    Try(
      Await.result(f, duration)
    ) match {
      case Success(response) => {
        sys.error("Expected function to fail but it succeeded with: " + response)
      }
      case Failure(ex) =>  ex match {
        case e: ErrorsResponse => {
          e
        }
        case e => {
          sys.error(s"Expected an exception of type[ErrorsResponse] but got[$e]")
        }
      }
    }
  }

  def expectNotFound[T](
    f: => Future[T],
    duration: Duration = DefaultDuration
  ) {
    expectStatus(404) {
      Await.result(f, duration)
    }
  }

  def expectNotAuthorized[T](
    f: => Future[T],
    duration: Duration = DefaultDuration
  ) {
    expectStatus(401) {
      Await.result(f, duration)
    }
  }

  def expectStatus(code: Int)(f: => Unit) {
    assert(code >= 400, s"code[$code] must be >= 400")

    Try(
      f
    ) match {
      case Success(response) => {
        org.specs2.execute.Failure(s"Expected HTTP[$code] but got HTTP 2xx")
      }
      case Failure(ex) => ex match {
        case UnitResponse(code) => {
          org.specs2.execute.Success()
        }
        case e => {
          org.specs2.execute.Failure(s"Unexpected error: $e")
        }
      }
    }
  }
}

