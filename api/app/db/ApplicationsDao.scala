package db

import io.flow.play.util.{IdGenerator, UrlKey}
import io.flow.registry.api.lib.DefaultPortAllocator
import io.flow.registry.v0.models.{Application, ApplicationForm, ApplicationPutForm, Service, Port}
import io.flow.registry.v0.models.json._
import io.flow.postgresql.{Authorization, Query, OrderBy, Pager}
import io.flow.common.v0.models.UserReference
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import io.flow.registry.v0.anorm.conversions.Standard._
import io.flow.registry.v0.anorm.conversions.Types._

object ApplicationsDao {

  private[this] val urlKey = UrlKey(minKeyLength = 3)
  private[this] val SortByPort = "(select min(external) from ports where application_id = applications.id)"

  private[this] val BaseQuery = Query(s"""
    select applications.id,
           applications.ports,
           applications.dependencies
      from applications
  """)

  private[this] val InsertQuery = """
    insert into applications
    (id, ports, dependencies, updated_by_user_id)
    values
    ({id}, {ports}::json, {dependencies}::json, {updated_by_user_id})
  """

  private[this] val UpdateQuery = """
    update applications
       set ports = {ports}::json,
           dependencies = {dependencies}::json,
           updated_by_user_id = {updated_by_user_id}
     where id = {id}
  """

  private[this] val dbHelpers = DbHelpers("applications")

  private[db] def validate(
    id: String,
    form: ApplicationPutForm,
    existing: Option[Application] = None
  ): Seq[String] = {
    val idErrors = if (id.trim.isEmpty) {
      Seq("Id cannot be empty")
    } else {
      ApplicationsDao.findById(Authorization.All, id) match {
        case None => {
          urlKey.validate(id)
        }
        case Some(application) => {
          Some(application.id) == existing.map(_.id) match {
            case true => Nil
            case false => Seq("Application with this id already exists")
          }
        }
      }
    }

    val serviceErrors = form.service match {
      case None => {
        Nil
      }
      case Some(service) => {
        ServicesDao.findById(Authorization.All, service) match {
          case None => {
            Seq("Service not found")
          }
          case _ => {
            Nil
          }
        }
      }
    }

    val dependencyErrors = form.dependency match {
      case None => {
        Nil
      }
      case Some(deps) => {
        deps.flatMap { dependencyId =>
          dependencyId == id.trim match {
            case true => {
              Seq(s"Cannot declare dependency[$dependencyId] on self")
            }
            case false => {
              ApplicationsDao.findById(Authorization.All, dependencyId) match {
                case None => {
                  Seq(s"Dependency[$dependencyId] references a non existing application")
                }
                case Some(app) => {
                  Nil
                }
              }
            }
          }
        }
      }
    }

    val circularDependencyErrors = existing match {
      case None => {
        Nil
      }
      case Some(app) => {
        form.dependency match {
          case None => {
            Nil
          }
          case Some(deps) => {
            val circularDependency = Pager.create { offset =>
              DependenciesDao.findAll(
                Authorization.All,
                dependencies = Some(Seq(app.id)),
                offset = offset
              )
            }.toList.find { dep => deps.contains(dep.applicationId) }

            circularDependency match {
              case None => {
                Nil
              }
              case Some(dep) => {
                Seq(s"Application[${app.id}] Cannot declare a circular dependency on[${dep.applicationId}]")
              }
            }
          }
        }
      }
    }

    val externalErrors = form.external match {
      case None => {
        Nil
      }
      case Some(port) => {
        ServicesDao.validatePort("External port", port) match {
          case Nil => {
            ApplicationsDao.findByPortNumber(Authorization.All, port) match {
              case None => {
                Nil
              }
              case Some(app) => {
                Seq(s"External port ${port} is already assigned to the application ${app.id}")
              }
            }
          }
          case errors => errors
        }
      }
    }

    val internalErrors = form.internal match {
      case None => {
        Nil
      }
      case Some(port) => {
        (port > 0) match {
          case true => Nil
          case false => Seq("Internal port must be > 0")
        }
      }
    }

    idErrors ++ serviceErrors ++ dependencyErrors ++ circularDependencyErrors ++ externalErrors ++ internalErrors
  }

  def create(createdBy: UserReference, form: ApplicationForm): Either[Seq[String], Application] = {
    val putForm = ApplicationPutForm(
      service = Some(form.service),
      external = form.external,
      internal = form.internal,
      dependency = form.dependency
    )

    validate(form.id, putForm) match {
      case Nil => {
        DB.withTransaction { implicit c =>
          val id = form.id.trim
          createPort(
            c,
            createdBy = createdBy,
            applicationId = id,
            internal = form.internal,
            external = form.external,
            serviceId = form.service
          )

          form.dependency match {
            case None => // Intentional no-op
            case Some(deps) => {
              deps.foreach { depId => createDependency(c, createdBy, id, depId) }
            }
          }

          SQL(InsertQuery).on(
            'id -> id,
            'ports -> portsAsJson(c, id),
            'dependencies -> dependenciesAsJson(c, id),
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

  def update(createdBy: UserReference, app: Application, form: ApplicationPutForm): Either[Seq[String], Application] = {
    validate(app.id, form, Some(app)) match {
      case Nil => {
        val newDependencies = form.dependency match {
          case None => Nil
          case Some(deps) => deps.filter { !app.dependencies.contains(_) }
        }

        DB.withTransaction { implicit c =>
          form.service.foreach { service =>
            if (!app.ports.map(_.service.id).contains(service)) {
              createPort(
                c,
                createdBy = createdBy,
                applicationId = app.id,
                external = form.external,
                internal = form.internal,
                serviceId = service
              )
            }
          }

          newDependencies.foreach { dep => createDependency(c, createdBy, app.id, dep) }

          // Update the applications table to trigger the journal
          // write.
          SQL(UpdateQuery).on(
            'id -> app.id,
            'ports -> portsAsJson(c, app.id),
            'dependencies -> dependenciesAsJson(c, app.id),
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

  /**
    * Fetches all dependencies from the dependencies table and returns as a JSON
    * string for denormalization in the applications table.
    */
  private[this] def dependenciesAsJson(implicit c: java.sql.Connection, applicationId: String): String = {
    Json.toJson(
      Pager.create { offset =>
        DependenciesDao.findAllWithConnection(
          c,
          Authorization.All,
          applications = Some(Seq(applicationId)),
          offset = offset
        )
      }.toSeq.map(_.dependencyId)
    ).toString
  }

  private[this] def createPort(
    implicit c: java.sql.Connection,
    createdBy: UserReference,
    applicationId: String,
    external: Option[Long],
    internal: Option[Long],
    serviceId: String
  ) {
    ServicesDao.findById(Authorization.All, serviceId).map { service =>
      PortsDao.create(
        c,
        createdBy,
        PortForm(
          applicationId = applicationId,
          serviceId = service.id,
          internal = internal.getOrElse(service.defaultPort),
          external = external.getOrElse(DefaultPortAllocator(applicationId, service.id).number)
        )
      )
    }
  }

  private[this] def createDependency(
    implicit c: java.sql.Connection,
    createdBy: UserReference,
    applicationId: String,
    dependencyId: String
  ) {
    DependenciesDao.create(
      c,
      createdBy,
      DependencyForm(
        applicationId = applicationId,
        dependencyId = dependencyId
      )
    )
  }

  def delete(deletedBy: UserReference, application: Application) {
    DB.withTransaction { implicit c =>
      Pager.create { offset =>
        PortsDao.findAllWithConnection(c, Authorization.User(deletedBy.id), applications = Some(Seq(application.id)), offset = offset).map { port =>
          PortsDao.delete(c, deletedBy, port)
        }
      }.toSeq

      Pager.create { offset =>
        DependenciesDao.findAllWithConnection(c, Authorization.User(deletedBy.id), applications = Some(Seq(application.id)), offset = offset).map { port =>
          DependenciesDao.delete(c, deletedBy, port)
        }
      }.toSeq

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
    * have any ports/dependencies in which case anorm will NOT find
    * the column named ports/dependencies.
    */
  private[this] def parser(
    id: String = "id",
    ports: String = "ports",
    dependencies: String = "dependencies"
  ): RowParser[io.flow.registry.v0.models.Application] = {
    SqlParser.str(id) ~
    SqlParser.get[Seq[JsObject]](ports).? ~
    SqlParser.get[Seq[JsObject]](dependencies).? map {
      case id ~ ports ~ dependencies => {
        io.flow.registry.v0.models.Application(
          id = id,
          ports = ports.getOrElse(Nil).map { _.as[Port] },
          dependencies = dependencies.getOrElse(Nil).map { _.as[String] }
        )
      }
    }
  }

}
