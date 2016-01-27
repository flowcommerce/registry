package db

import io.flow.common.v0.models.{ChangeType, User}
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.registry.v0.models.ApplicationVersion
import org.joda.time.DateTime
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

object ApplicationVersionsDao {

  private[this] val BaseQuery = Query(s"select * from journal.applications")

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("journal_timestamp", Some("applications"))
  ): Seq[ApplicationVersion] = {
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("applications.journal_id", ids.map(_.map(_.toLong))).
        optionalIn("applications.id", applications).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          parser().*
        )
    }
  }

  private[this] def parser(): RowParser[ApplicationVersion] = {
    SqlParser.get[Long]("journal_id") ~
    SqlParser.get[DateTime]("journal_timestamp") ~
    SqlParser.get[String]("journal_operation") ~
    io.flow.registry.v0.anorm.parsers.Application.parser() map {
      case id ~ ts ~ op ~ application => {
        ApplicationVersion(
          id = id.toString,
          timestamp = ts,
          `type` = ChangeType(op),
          application = application
        )
      }
    }
  }
}
