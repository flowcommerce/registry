package db

import io.flow.common.v0.models.{ExpandableUser, ExpandableUserUndefinedType, User, UserReference}
import anorm._
import play.api.db._
import play.api.Play.current

case class DbHelpers(tableName: String) {

  private[this] val SoftDeleteQuery = s"""
    update $tableName set deleted_at=now(), updated_by_user_id = {updated_by_user_id} where id = {id}
  """

  def delete(deletedBy: ExpandableUser, id: String) {
    DB.withConnection { implicit c =>
      delete(c, deletedBy, id)
    }
  }

  def delete(
    implicit c: java.sql.Connection,
    deletedBy: ExpandableUser,
    id: String
  ) {
    SQL(SoftDeleteQuery).on(
      'id -> id,
      'updated_by_user_id -> userId(deletedBy)
    ).execute()
  }

  def userId(user: ExpandableUser): String = {
    user match {
      case o: User => o.id
      case UserReference(id) => id
      case ExpandableUserUndefinedType(other) => sys.error(s"Invalid org[$other]")
    }
  }
  
}
