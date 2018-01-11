package util

import java.util.concurrent.TimeUnit

import io.flow.common.v0.models.UserReference
import io.flow.play.util.{AuthHeaders, FlowSession}
import io.flow.registry.v0.Client
import io.flow.registry.v0.errors.{GenericErrorResponse, UnitResponse}
import play.api.Application

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

trait MockClient extends FlowPlaySpec {

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)

  override lazy val port = 9010

  lazy val anonClient = new io.flow.registry.v0.Client(wsClient, s"http://localhost:$port")

  def identifiedClient(
                        user: UserReference = testUser,
                        org: Option[String] = None,
                        session: Option[FlowSession] = None
                      )(implicit app: Application): Client = {
    val auth = org match {
      case None => AuthHeaders.user(user, session = session)
      case Some(o) => AuthHeaders.organization(user, o, session = session)
    }

    new Client(
      wsClient,
      s"http://localhost:$port",
      defaultHeaders = authHeaders.headers(auth)
    )
  }

  def expectErrors[T](
    f: => Future[T],
    duration: Duration = DefaultDuration
  ): GenericErrorResponse = {
    Try(
      Await.result(f, duration)
    ) match {
      case Success(response) => {
        sys.error("Expected function to fail but it succeeded with: " + response)
      }
      case Failure(ex) =>  ex match {
        case e: GenericErrorResponse => {
          e
        }
        case e => {
          sys.error(s"Expected an exception of type[GenericErrorResponse] but got[$e]")
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

