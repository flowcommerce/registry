package lib

import io.flow.postgresql.Authorization

/** Maps Authorization.PublicOnly -> Authorization.All
  */
trait PublicAuthorizedQuery {

  def queryAuth(auth: Authorization): Authorization = {
    auth match {
      case Authorization.PublicOnly => Authorization.All
      case _ => Authorization.All // TODO: Implement auth
    }
  }

}
