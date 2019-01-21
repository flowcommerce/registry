package db

import javax.inject.{Inject, Singleton}

import anorm._
import io.flow.common.v0.models.UserReference
import io.flow.play.util.UrlKey
import io.flow.postgresql.play.db.DbHelpers
import io.flow.postgresql.{Authorization, OrderBy, Pager, Query}
import io.flow.registry.api.lib.DefaultPortAllocator
import io.flow.registry.v0.models.json._
import io.flow.registry.v0.models.{Application, ApplicationForm, ApplicationPutForm}
import play.api.db._
import play.api.libs.json._

@Singleton
class ApplicationsDao @Inject()(
  servicesDao: ServicesDao,
  dependenciesDao: DependenciesDao,
  portsDao: PortsDao,
  db: Database
) extends lib.PublicAuthorizedQuery {

  private[this] val dbHelpers = DbHelpers(db, "applications")
  private[this] val urlKey = UrlKey(minKeyLength = 2)
  private[this] val SortByPort = "(select min(external) from ports where application_id = applications.id)"

  private[this] val BaseQuery = Query(
    s"""
    select applications.id,
           applications.ports,
           applications.dependencies
      from applications
  """)

  private[this] val InsertQuery =
    """
    insert into applications
    (id, ports, dependencies, updated_by_user_id)
    values
    ({id}, {ports}::json, {dependencies}::json, {updated_by_user_id})
  """

  private[this] val UpdateQuery =
    """
    update applications
       set ports = {ports}::json,
           dependencies = {dependencies}::json,
           updated_by_user_id = {updated_by_user_id}
     where id = {id}
  """

  private[db] def validate(
    id: String,
    form: ApplicationPutForm,
    existing: Option[Application] = None
  ): Seq[String] = {
    val idErrors = if (id.trim.isEmpty) {
      Seq("Id cannot be empty")
    } else {
      findById(Authorization.All, id) match {
        case None => {
          urlKey.validate(id)
        }
        case Some(application) => {
          if (existing.map(_.id).contains(application.id)) {
            Nil
          } else {
            Seq("Application with this id already exists")
          }
        }
      }
    }

    val serviceErrors = form.service match {
      case None => {
        Nil
      }
      case Some(service) => {
        servicesDao.findById(Authorization.All, service) match {
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
          if (dependencyId == id.trim) {
            Seq(s"Cannot declare dependency[$dependencyId] on self")
          } else {
            findById(Authorization.All, dependencyId) match {
              case None => {
                Seq(s"Dependency[$dependencyId] references a non existing application")
              }
              case Some(_) => {
                Nil
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
              dependenciesDao.findAll(
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
        servicesDao.validatePort("External port", port) match {
          case Nil => {
            findByPortNumber(Authorization.All, port) match {
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
        if (port > 0) {
          Nil
        } else {
          Seq("Internal port must be > 0")
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
        db.withTransaction { implicit c =>
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

  def upsertDependency(createdBy: UserReference, app: Application, dependency: String): Either[Seq[String], Application] = {
    findById(Authorization.User(createdBy.id), dependency) match {
      case None => {
        Left(Seq(s"Application named[$dependency] not found"))
      }

      case Some(dep) => {
        val putForm = ApplicationPutForm(
          dependency = Some(app.dependencies ++ Seq(dep.id))
        )
        update(createdBy, app, putForm) match {
          case Left(errors) => sys.error(s"Invalid error when updating dependencies: $errors")
          case Right(updated) => Right(updated)
        }
      }
    }
  }

  def removeDependency(createdBy: UserReference, app: Application, dependency: String): Either[Seq[String], Application] = {
    findById(Authorization.User(createdBy.id), dependency) match {
      case None => {
        Left(Seq(s"Application named[$dependency] not found"))
      }

      case Some(_) => {
        val putForm = ApplicationPutForm(
          dependency = Some(app.dependencies.filter(_ != dependency))
        )

        update(createdBy, app, putForm) match {
          case Left(errors) => sys.error(s"Invalid error when updating dependencies: $errors")
          case Right(updated) => Right(updated)
        }
      }
    }
  }

  def update(createdBy: UserReference, app: Application, form: ApplicationPutForm): Either[Seq[String], Application] = {
    validate(app.id, form, Some(app)) match {
      case Nil => {
        val dependenciesToAdd = form.dependency match {
          case None => Nil
          case Some(deps) => deps.filter {
            !app.dependencies.contains(_)
          }
        }

        val dependenciesToDelete = form.dependency match {
          case None => Nil
          case Some(deps) => app.dependencies.filter {
            !deps.contains(_)
          }
        }

        db.withTransaction { implicit c =>
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

          dependenciesToDelete.foreach { dep => dependenciesDao.deleteApplicationDependency(c, createdBy, app.id, dep) }
          dependenciesToAdd.foreach { dep => createDependency(c, createdBy, app.id, dep) }

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
        portsDao.findAllWithConnection(
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
        dependenciesDao.findAllWithConnection(
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
  ): Unit = {
    servicesDao.findById(Authorization.All, serviceId).foreach { service =>
      portsDao.create(
        c,
        createdBy,
        PortForm(
          applicationId = applicationId,
          serviceId = service.id,
          internal = internal.getOrElse(service.defaultPort),
          external = external.getOrElse(new DefaultPortAllocator(this, portsDao).number(applicationId, service.id))
        )
      )
    }
  }

  private[this] def createDependency(
    implicit c: java.sql.Connection,
    createdBy: UserReference,
    applicationId: String,
    dependencyId: String
  ): Unit = {
    dependenciesDao.create(
      c,
      createdBy,
      DependencyForm(
        applicationId = applicationId,
        dependencyId = dependencyId
      )
    )
    ()
  }

  def delete(deletedBy: UserReference, application: Application): Unit = {
    db.withTransaction { implicit c =>
      Pager.create { offset =>
        portsDao.findAllWithConnection(c, Authorization.User(deletedBy.id), applications = Some(Seq(application.id)), offset = offset)
      }.toSeq.foreach {port =>
        portsDao.delete(c, deletedBy, port)
      }

      Pager.create { offset =>
        dependenciesDao.findAllWithConnection(c, Authorization.User(deletedBy.id), applications = Some(Seq(application.id)), offset = offset)
      }.toSeq.foreach { port =>
        dependenciesDao.delete(c, deletedBy, port)
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
    val sortSql = if (orderBy.sql.contains("port")) {
      Some(SortByPort)
    } else if (orderBy.sql.contains("port desc")) {
      Some(s"$SortByPort desc")
    } else {
      orderBy.sql
    }

    db.withConnection { implicit c =>
      dbHelpers.authorizedQuery(BaseQuery, queryAuth(auth)).
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
          prefix.map { _ =>
            s"(applications.id = {prefix} or applications.id like {prefix} || '-%')"
          }
        ).bind("prefix", prefix).
        and(
          q.map { _ =>
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

}
