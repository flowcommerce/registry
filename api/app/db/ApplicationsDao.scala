package db

import io.flow.play.util.{IdGenerator, UrlKey}
import io.flow.registry.api.lib.DefaultPortAllocator
import io.flow.registry.v0.models.{Application, ApplicationForm, Service, Port}
import io.flow.registry.v0.models.json._
import io.flow.postgresql.{Authorization, Query, OrderBy, Pager}
import io.flow.common.v0.models.User
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import io.flow.registry.v0.anorm.conversions.Json._

object ApplicationsDao {

  private[this] val urlKey = UrlKey(minKeyLength = 3)
  private[this] val SortByPort = "(select min(external) from ports where application_id = applications.id)"

  private[this] val BaseQuery = Query(s"""
    select applications.id,
           applications.ports
      from applications
  """)

  private[this] val InsertQuery = """
    insert into applications
    (id, ports, updated_by_user_id)
    values
    ({id}, {ports}::json, {updated_by_user_id})
  """

  private[this] val UpdateQuery = """
    update applications
       set ports = {ports}::json,
           updated_by_user_id = {updated_by_user_id}
     where id = {id}
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

    val serviceErrors = ServicesDao.findById(Authorization.All, form.service) match {
      case None => {
        Seq("Service not found")
      }
      case _ => {
        Nil
      }
    }

    val portErrors = form.port match {
      case None => {
        Nil
      }
      case Some(port) => {
        ServicesDao.validatePort(port) match {
          case Nil => {
            ApplicationsDao.findByPortNumber(Authorization.All, port) match {
              case None => {
                Nil
              }
              case Some(app) => {
                Seq(s"Port ${port} is already assigned to the application ${app.id}")
              }
            }
          }
          case errors => errors
        }
      }
    }

    idErrors ++ serviceErrors ++ portErrors
  }

  def create(createdBy: User, form: ApplicationForm): Either[Seq[String], Application] = {
    validate(form) match {
      case Nil => {
        DB.withTransaction { implicit c =>
          val id = form.id.trim
          createPort(c, createdBy, id, form.port, form.service)

          SQL(InsertQuery).on(
            'id -> id,
            'ports -> portsAsJson(c, id),
            'updated_by_user_id -> createdBy.id
          ).execute()
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

  def update(createdBy: User, app: Application, form: ApplicationForm): Either[Seq[String], Application] = {
    validate(form, Some(app)) match {
      case Nil => {
        val newService = !app.ports.map(_.service.id).contains(form.service)

        DB.withTransaction { implicit c =>
          if (newService) {
            createPort(c, createdBy, app.id, form.port, form.service)
          }

          // Update the applications table to trigger the journal
          // write.
          SQL(UpdateQuery).on(
            'id -> app.id,
            'ports -> portsAsJson(c, app.id),
            'updated_by_user_id -> createdBy.id
          ).execute()

        }
        Right(
          findById(Authorization.All, app.id).getOrElse {
            sys.error("Failed to update application")
          }
        )
      }
      case errors => Left(errors)
    }
  }

  /**
    * Fetches all ports from the ports table and returns as a JSON
    * string for denormalization in the applications table.
    */
  private[this] def portsAsJson(implicit c: java.sql.Connection, applicationId: String): String = {
    Json.toJson(
      Pager.create { offset =>
        PortsDao.findAllWithConnection(
          c,
          Authorization.All,
          applications = Some(Seq(applicationId)),
          offset = offset
        )
      }.toSeq.map(_.port)
    ).toString
  }

  private[this] def createPort(
    implicit c: java.sql.Connection,
    createdBy: User,
    applicationId: String,
    internalPort: Option[Long],
    serviceId: String
  ) {
    ServicesDao.findById(Authorization.All, serviceId).map { service =>
      PortsDao.create(
        c,
        createdBy,
        PortForm(
          applicationId = applicationId,
          serviceId = service.id,
          internal = internalPort.getOrElse(service.defaultPort),
          external = DefaultPortAllocator(applicationId, service.id).number
        )
      )
    }
  }

  def delete(deletedBy: User, application: Application) {
    DB.withTransaction { implicit c =>
      Pager.create { offset =>
        PortsDao.findAllWithConnection(c, Authorization.User(deletedBy.id), applications = Some(Seq(application.id)), offset = offset).map { port =>
          PortsDao.delete(c, deletedBy, port)
        }
      }
      dbHelpers.delete(c, deletedBy, application.id)
    }
  }

  def findByPortNumber(auth: Authorization, num: Long): Option[Application] = {
    findAll(auth, portNumbers = Some(Seq(num)), limit = 1).headOption
  }

  def findById(auth: Authorization, id: String): Option[Application] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    services: Option[Seq[String]] = None,
    portNumbers: Option[Seq[Long]] = None,
    prefix: Option[String] = None,
    q: Option[String] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("-created_at", Some("applications"))
  ): Seq[Application] = {
    // TODO: Auth

    val sortSql = if (orderBy.sql == Some("port")) {
      Some(SortByPort)
    } else if (orderBy.sql == Some("port desc")) {
      Some(s"$SortByPort desc")
    } else {
      orderBy.sql
    }

    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("applications.id", ids).
        and(
          services.map { ids =>
            // TODO: bind variables
            s"applications.id in (select application_id from ports where service_id in (%s))".format(ids.mkString("'", "', '", "'"))
          }
        ).
        and(
          portNumbers.map { nums =>
            // TODO: bind variables
            s"applications.id in (select application_id from ports where external in (%s))".format(nums.mkString(", "))
          }
        ).
        and(
          prefix.map { p =>
            s"(applications.id = {prefix} or applications.id like {prefix} || '-%')"
          }
        ).bind("prefix", prefix).
        and(
          q.map { q =>
            s"applications.id like '%' || lower(trim({q})) || '%'"
          }
        ).bind("q", q).
        limit(limit).
        offset(offset).
        orderBy(sortSql).
        as(
          io.flow.registry.v0.anorm.parsers.Application.parser().*
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
          ports = ports.getOrElse(Nil).map { _.as[Port] }
        )
      }
    }
  }

}
