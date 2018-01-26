package db

import javax.inject.{Inject, Singleton}

import anorm._
import io.flow.common.v0.models.UserReference
import io.flow.play.util.UrlKey
import io.flow.postgresql.play.db.DbHelpers
import io.flow.postgresql.{Authorization, OrderBy, Query}
import io.flow.registry.v0.models.{Service, ServiceForm}
import play.api.db._

@Singleton
class ServicesDao @Inject() (
  @NamedDatabase("default") db: Database
){

  private val dbHelpers = DbHelpers(db, "services")

  private[this] val urlKey = UrlKey(minKeyLength = 3)
  private[this] val BaseQuery = Query(s"""
    select services.id,
           services.default_port
      from services
  """)

  private[this] val InsertQuery = """
    insert into services
    (id, default_port, updated_by_user_id)
    values
    ({id}, {default_port}, {updated_by_user_id})
  """

  private[this] val UpdateQuery = """
    update services
       set default_port = {default_port},
           updated_by_user_id = {updated_by_user_id}
     where id = {id}
  """

  private[db] def validate(
    form: ServiceForm,
    existing: Option[Service] = None
  ): Seq[String] = {
    val idErrors = if (form.id.trim.isEmpty) {
      Seq("Id cannot be empty")
    } else {
      findById(Authorization.All, form.id) match {
        case None => {
          urlKey.validate(form.id)
        }
        case Some(service) => {
          Some(service.id) == existing.map(_.id) match {
            case true => Nil
            case false => Seq("Service with this id already exists")
          }
        }
      }
    }

    idErrors ++ validatePort("Default port", form.defaultPort)
  }

  private[db] def validatePort(label: String, port: Long): Seq[String] = {
    if (port <= 1024) {
      Seq(s"$label must be > 1024")
    } else {
      Nil
    }
  }

  def create(createdBy: UserReference, form: ServiceForm): Either[Seq[String], Service] = {
    validate(form) match {
      case Nil => {
        db.withConnection { implicit c =>
          val id = form.id.trim

          SQL(InsertQuery).on(
            'id -> id,
            'default_port -> form.defaultPort,
            'updated_by_user_id -> createdBy.id
          ).execute()
        }

        Right(
          findById(Authorization.All, form.id.trim).getOrElse {
            sys.error("Failed to create service")
          }
        )
      }
      case errors => Left(errors)
    }
  }

  def update(createdBy: UserReference, existing: Service, form: ServiceForm): Either[Seq[String], Service] = {
    validate(form, existing = Some(existing)) match {
      case Nil => {
        db.withConnection { implicit c =>
          SQL(UpdateQuery).on(
            'id -> existing.id,
            'default_port -> form.defaultPort,
            'updated_by_user_id -> createdBy.id
          ).execute()
        }

        Right(
          findById(Authorization.All, existing.id).getOrElse {
            sys.error("Failed to update service")
          }
        )
      }
      case errors => Left(errors)
    }
  }

  def delete(deletedBy: UserReference, service: Service) {
    dbHelpers.delete(deletedBy, service.id)
  }

  def findById(auth: Authorization, id: String): Option[Service] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    defaultPortNumbers: Option[Seq[Long]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("-created_at", Some("services"))
  ): Seq[Service] = {
    // TODO: Auth

    db.withConnection { implicit c =>
      BaseQuery.
        optionalIn("services.id", ids).
        optionalIn("services.default_port", defaultPortNumbers).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          io.flow.registry.v0.anorm.parsers.Service.parser().*
        )
    }
  }

}
