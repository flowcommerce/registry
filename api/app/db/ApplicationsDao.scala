package db

import io.flow.play.util.{IdGenerator, UrlKey}
import io.flow.registry.api.lib.DefaultPortAllocator
import io.flow.registry.v0.models.{Application, ApplicationForm, ApplicationPutForm, PortType, Port}
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.common.v0.models.User
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import java.util.UUID

import io.flow.registry.v0.anorm.conversions.Json._

object ApplicationsDao {

  private[this] val urlKey = UrlKey(minKeyLength = 4)

  private[this] val BaseQuery = Query(s"""
    select applications.id,
           to_json(
             array(
               (select row_to_json(ports.*) from ports where application_id = applications.id and deleted_at is null order by number)
             )
           ) as ports
      from applications
  """)

  private[this] val InsertQuery = """
    insert into applications
    (id, updated_by_user_id)
    values
    ({id}, {updated_by_user_id})
  """

  private[this] val DeleteIdQuery = """
    update applications set id = {deleted_id} where id = {id}
  """

  private[this] val dbHelpers = DbHelpers("applications")

  private[db] def validate(
    form: ApplicationForm,
    existing: Option[Application] = None
  ): Seq[String] = {
    val idErrors = if (form.id.trim.isEmpty) {
      Seq("Id cannot be empty")
    } else {
      ApplicationsDao.findById(Authorization.All, form.id) match {
        case None => {
          urlKey.validate(form.id)
        }
        case Some(application) => {
          Some(application.id) == existing.map(_.id) match {
            case true => Nil
            case false => Seq("Application with this id already exists")
          }
        }
      }
    }

    val typeErrors = form.`type`.flatMap {
      case PortType.UNDEFINED(_) => {
        Some("Invalid application type. Must be one of: " + PortType.all.map(_.toString).sorted.mkString(", "))
      }
      case _ => {
        None
      }
    }.distinct

    idErrors ++ typeErrors
  }

  def create(createdBy: User, form: ApplicationForm): Either[Seq[String], Application] = {
    validate(form) match {
      case Nil => {
        DB.withTransaction { implicit c =>
          val id = form.id.trim
          SQL(InsertQuery).on(
            'id -> id,
            'updated_by_user_id -> createdBy.id
          ).execute()

          form.`type`.map { t =>
            PortsDao.create(
              c, createdBy, PortForm(
                applicationId = id,
                typ = t,
                number = DefaultPortAllocator(form.id, t).number
              )
            )
          }
        }

        Right(
          findById(Authorization.All, form.id.trim).getOrElse {
            sys.error("Failed to create application")
          }
        )
      }
      case errors => Left(errors)
    }
  }

  def upsert(createdBy: User, id: String, form: ApplicationPutForm): Either[Seq[String], Application] = {
    // TODO: Allocate any new ports
    findById(Authorization.All, id) match {
      case Some(app) => Right(app)
      case None => create(createdBy, ApplicationForm(id = id, `type` = form.`type`))
    }
  }

  def softDelete(deletedBy: User, application: Application) {
    // TODO: Should we remove ports?
    // TODO: How should we allow for resuse of the application id?
    dbHelpers.delete(deletedBy, application.id)
  }

  def findByPortNumber(auth: Authorization, number: Long): Option[Application] = {
    findAll(auth, portNumbers = Some(Seq(number)), limit = 1).headOption
  }

  def findById(auth: Authorization, id: String): Option[Application] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  // TODO: Should we add a filter by port number?
  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    portNumbers: Option[Seq[Long]] = None,
    prefix: Option[String] = None,
    isDeleted: Option[Boolean] = Some(false),
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("-created_at", Some("applications"))
  ): Seq[Application] = {
    // TODO: Auth
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("applications.id", ids).
        and(
          portNumbers.map { numbers =>
            // TODO: bind variables
            s"applications.id in (select application_id from ports where deleted_at is null and number in (%s))".format(numbers.mkString(", "))
          }
        ).
        nullBoolean("applications.deleted_at", isDeleted).
        and(
          prefix.map { p =>
            s"(applications.id = {prefix} or applications.id like {prefix} || '-%')"
          }
        ).bind("prefix", prefix).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          parser().*
        )
    }
  }

  /**
    * Write custom parser as it is possible for an application to not
    * have any ports in which case anorm will NOT find the column
    * named ports.
    */
  private[this] def parser(
    id: String = "id",
    ports: String = "ports"
  ): RowParser[io.flow.registry.v0.models.Application] = {
    SqlParser.str(id) ~
    SqlParser.get[Seq[JsObject]](ports).? map {
      case id ~ ports => {
        io.flow.registry.v0.models.Application(
          id = id,
          ports = ports.getOrElse(Nil).map { js =>
            Port(
              `type` = PortType( (js \ "type").as[String] ),
              number = (js \ "number").as[Long]
            )
          }
        )
      }
    }
  }

}
