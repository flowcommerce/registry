package db

import io.flow.common.v0.models.{ChangeType, User}
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.registry.v0.models.ServiceVersion
import org.joda.time.DateTime
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

object ServiceVersionsDao {

  private[this] val BaseQuery = Query("""
    select services.*
      from journal.services
  """.stripMargin)

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    services: Option[Seq[String]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("journal_timestamp", Some("services"))
  ): Seq[ServiceVersion] = {
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("services.journal_id", ids.map(_.map(_.toLong))).
        optionalIn("services.id", services).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          parser().*
        )
    }
  }

  private[this] def parser(): RowParser[ServiceVersion] = {
    SqlParser.get[Long]("journal_id") ~
    SqlParser.get[DateTime]("journal_timestamp") ~
    SqlParser.get[String]("journal_operation") ~
    io.flow.registry.v0.anorm.parsers.Service.parser() map {
      case id ~ ts ~ op ~ service => {
        ServiceVersion(
          id = id.toString,
          timestamp = ts,
          `type` = ChangeType(op),
          service = service
        )
      }
    }
  }
}
