package db

import io.flow.common.v0.models.User
import io.flow.play.util.IdGenerator
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.registry.v0.models.Port
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import java.util.UUID

case class PortForm(
  applicationId: String,
  number: Long
)

object PortsDao {

  private[this] val BaseQuery = Query(s"""
    select ports.id,
           ports.application_id,
           ports.number
      from ports
  """)

  private[this] val InsertQuery = """
    insert into ports
    (id, application_id, number, updated_by_user_id)
    values
    ({id}, {application_id}, {number}, {updated_by_user_id})
  """

  private[this] val dbHelpers = DbHelpers("ports")
  private[this] val idGenerator = IdGenerator("prt")

  private[db] def validate(
    form: PortForm
  ): Seq[String] = {
    val portErrors = if (form.number <= 1024) {
      Seq("Port must be > 1024")
    } else {
      PortsDao.findByNumber(Authorization.All, form.number) match {
        case None => {
          Nil
        }
        case Some(port) => {
          Seq(s"Port ${form.number} is already assigned to the application ${port.applicationId}")
        }
      }
    }

    val applicationErrors = ApplicationsDao.findById(Authorization.All, form.applicationId) match {
      case None => Seq("Application not found")
      case Some(_) => Nil
    }

    portErrors ++ applicationErrors
  }

  def create(createdBy: User, form: PortForm): Either[Seq[String], Port] = {
    validate(form) match {
      case Nil => {
        val id = DB.withConnection { implicit c =>
          create(c, createdBy, form)
        }
        Right(
          findById(Authorization.All, id).getOrElse {
            sys.error("Failed to create port")
          }
        )
      }
      case errors => Left(errors)
    }
  }

  private[db] def create(
    implicit c: java.sql.Connection,
    createdBy: User, form: PortForm
  ): String = {
    val id = idGenerator.randomId()
    SQL(InsertQuery).on(
      'id -> id,
      'application_id -> form.applicationId,
      'number -> form.number,
      'updated_by_user_id -> createdBy.id
    ).execute()
    id
  }

  def softDelete(deletedBy: User, port: Port) {
    dbHelpers.delete(deletedBy, port.id)
  }

  def findByNumber(auth: Authorization, number: Long): Option[Port] = {
    findAll(auth, numbers = Some(Seq(number)), limit = 1).headOption
  }

  def findById(auth: Authorization, id: String): Option[Port] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    numbers: Option[Seq[Long]] = None,
    isDeleted: Option[Boolean] = Some(false),
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("-created_at", Some("ports"))
  ): Seq[Port] = {
    // TODO: Auth
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("ports.id", ids).
        optionalIn("ports.number", numbers).
        nullBoolean("ports.deleted_at", isDeleted).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          parser().*
        )
    }
  }

  def parser() = {
    SqlParser.str("id") ~
    SqlParser.str("application_id") ~
    SqlParser.long("number") map {
      case id ~ applicationId ~ number => {
        Port(
          id = id,
          applicationId = applicationId,
          number = number
        )
      }
    }
  }

}
