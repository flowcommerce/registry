package controllers

import io.flow.registry.v0.{Authorization, Client}
import io.flow.registry.v0.errors.{ErrorsResponse, UnitResponse}
import io.flow.play.clients.MockUserTokensClient
import io.flow.common.v0.models.UserReference
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
import java.util.concurrent.TimeUnit

trait MockClient extends db.Helpers {

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)

  val port = 9010

  lazy val anonClient = new Client(s"http://localhost:$port")
  lazy val identifiedClient = makeIdentifiedClient(user = testUser)

  /**
    * Generates an instance of the client where the user has been
    * granted all privileges.
    */
  def makeIdentifiedClient(
    user: UserReference = MockUserTokensClient.makeUserReference(),
    token: String = createTestId()
  ): Client = {
    MockUserTokensClient.add(user, token = Some(token))
    new Client(
      s"http://localhost:$port",
      auth = Some(Authorization.Basic(token))
    )
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
        _root_.org.specs2.execute.Failure(s"Expected HTTP[$code] but got HTTP 2xx")
      }
      case Failure(ex) => ex match {
        case UnitResponse(code) => {
          _root_.org.specs2.execute.Success()
        }
        case e => {
          _root_.org.specs2.execute.Failure(s"Unexpected error: $e")
        }
      }
    }
  }
}

