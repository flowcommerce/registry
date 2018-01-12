package db

import javax.inject.{Inject, Singleton}

import io.flow.common.v0.models.UserReference
import anorm._
import play.api.db._

@Singleton
class DbHelpers @Inject() (db: Database)  {

  private[this] def DeleteQuery(tableName: String) = s"""
    select util.delete_by_id({updated_by_user_id}, '$tableName', {id})
  """

  def delete(tableName: String, deletedBy: UserReference, id: String) {
    db.withConnection { implicit c =>
      delete(tableName)(c, deletedBy, id)
    }
  }

  def delete(tableName: String)(
    implicit c: java.sql.Connection,
    deletedBy: UserReference,
    id: String
  ) {
    SQL(DeleteQuery(tableName)).on(
      'updated_by_user_id -> deletedBy.id,
      'id -> id
    ).execute()
  }

}
