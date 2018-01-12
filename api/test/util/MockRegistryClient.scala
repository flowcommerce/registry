package util

import io.flow.common.v0.models.UserReference
import io.flow.play.util.{AuthHeaders, FlowSession}
import io.flow.registry.v0.Client
import io.flow.test.utils.FlowMockClient

trait MockRegistryClient extends FlowMockClient[
    io.flow.registry.v0.Client,
    io.flow.registry.v0.errors.GenericErrorResponse,
    io.flow.registry.v0.errors.UnitResponse
  ]{

  override def createAnonymousClient(baseUrl: String): Client =
    new io.flow.registry.v0.Client(
      ws = wsClient,
      baseUrl = baseUrl
    )

  override def createIdentifiedClient(baseUrl: String, user: UserReference, org: Option[String], session: Option[FlowSession]): Client = {
    val auth = org match {
      case None => AuthHeaders.user(user, session = session)
      case Some(o) => AuthHeaders.organization(user, o, session = session)
    }

    new io.flow.registry.v0.Client(
      ws = wsClient,
      baseUrl = baseUrl,
      defaultHeaders = authHeaders.headers(auth)
    )
  }
}

