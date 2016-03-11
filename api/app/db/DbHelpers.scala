package db

import io.flow.common.v0.models.UserReference
import anorm._
import play.api.db._
import play.api.Play.current

case class DbHelpers(tableName: String) {

  private[this] val DeleteQuery = s"""
    select util.delete_by_id({updated_by_user_id}, '$tableName', {id})
  """

  def delete(deletedBy: UserReference, id: String) {
    DB.withConnection { implicit c =>
      delete(c, deletedBy, id)
    }
  }

  def delete(
    implicit c: java.sql.Connection,
    deletedBy: UserReference,
    id: String
  ) {
    SQL(DeleteQuery).on(
      'updated_by_user_id -> deletedBy.id,
      'id -> id
    ).execute()
  }

}
