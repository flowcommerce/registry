package db

import io.flow.common.v0.models.{ExpandableUser, ExpandableUserUndefinedType, User, UserReference}
import anorm._
import play.api.db._
import play.api.Play.current

case class DbHelpers(tableName: String) {

  private[this] val DeleteQuery = s"""
    select util.delete_by_id({updated_by_user_id}, '$tableName', {id})
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
    SQL(DeleteQuery).on(
      'updated_by_user_id -> userId(deletedBy),
      'id -> id
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
