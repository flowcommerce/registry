package db

import io.flow.common.v0.models.User
import io.flow.play.util.IdGenerator
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.registry.v0.models.{PortType, Port}
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import java.util.UUID

case class PortForm(
  applicationId: String,
  typ: PortType,
  num: Long
)

private[db] case class InternalPort(
  id: String,
  applicationId: String,
  typ: PortType,
  num: Long
)

object PortsDao {

  private[this] val BaseQuery = Query(s"""
    select ports.id,
           ports.application_id,
           ports.type,
           ports.num
      from ports
  """)

  private[this] val InsertQuery = """
    insert into ports
    (id, application_id, type, num, updated_by_user_id)
    values
    ({id}, {application_id}, {type}, {num}, {updated_by_user_id})
  """

  private[this] val dbHelpers = DbHelpers("ports")
  private[this] val idGenerator = IdGenerator("prt")

  private[db] def validate(
    form: PortForm
  ): Seq[String] = {
    val portErrors = if (form.num <= 1024) {
      Seq("Port must be > 1024")
    } else {
      ApplicationsDao.findByPortNumber(Authorization.All, form.num) match {
        case None => {
          Nil
        }
        case Some(app) => {
          Seq(s"Port ${form.num} is already assigned to the application ${app.id}")
        }
      }
    }

    val applicationErrors = ApplicationsDao.findById(Authorization.All, form.applicationId) match {
      case None => Seq("Application not found")
      case Some(_) => Nil
    }

    portErrors ++ applicationErrors
  }

  def create(createdBy: User, form: PortForm): Either[Seq[String], InternalPort] = {
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
      'type -> form.typ.toString,
      'num -> form.num,
      'updated_by_user_id -> createdBy.id
    ).execute()
    id
  }

  def softDelete(implicit c: java.sql.Connection, deletedBy: User, port: InternalPort) {
    dbHelpers.delete(c, deletedBy, port.id)
  }

  def maxPortNumber(): Option[Long] = {
    PortsDao.findAll(Authorization.All, orderBy = OrderBy("-ports.num"), limit = 1).map(_.num).headOption
  }

  def findByNumber(auth: Authorization, num: Long): Option[InternalPort] = {
    findAll(auth, nums = Some(Seq(num)), limit = 1).headOption
  }

  def findById(auth: Authorization, id: String): Option[InternalPort] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  private[db] def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    nums: Option[Seq[Long]] = None,
    isDeleted: Option[Boolean] = Some(false),
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("ports.application_id, ports.num")
  ): Seq[InternalPort] = {
    // TODO: Auth
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("ports.id", ids).
        optionalIn("ports.application_id", applications).
        optionalIn("ports.num", nums).
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
    SqlParser.str("type") ~
    SqlParser.long("num") map {
      case id ~ applicationId ~ typ ~ num => {
        InternalPort(
          id = id,
          applicationId = applicationId,
          typ = PortType(typ),
          num = num
        )
      }
    }
  }

}
