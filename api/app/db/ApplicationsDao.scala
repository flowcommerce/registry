package db

import io.flow.play.util.UrlKey
import io.flow.registry.v0.models.{Application, ApplicationForm}
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.common.v0.models.User
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import java.util.UUID

object ApplicationsDao {

  private[this] val urlKey = UrlKey(minKeyLength = 4)

  private[this] val BaseQuery = Query(s"""
    select applications.id,
           (select port from ports where application_id = applications.id and deleted_at is null) as ports
      from applications
  """)

  private[this] val InsertQuery = """
    insert into applications
    (id, updated_by_user_id)
    values
    ({id}, {updated_by_user_id})
  """

  private[this] val dbHelpers = DbHelpers("applications")

  private[db] def validate(
    form: ApplicationForm,
    existing: Option[Application] = None
  ): Seq[String] = {
    if (form.id.trim.isEmpty) {
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
  }

  /**
    * Creates an application and allocates the requested number of
    * ports
    */
  def create(createdBy: User, form: ApplicationForm): Either[Seq[String], Application] = {
    validate(form) match {
      case Nil => {
        DB.withTransaction { implicit c =>
          SQL(InsertQuery).on(
            'id -> form.id.trim,
            'updated_by_user_id -> createdBy.id
          ).execute()

          // TODO: Allocate ports
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

  def upsert(createdBy: User, id: String): Either[Seq[String], Application] = {
    findById(Authorization.All, id) match {
      case Some(app) => Right(app)
      case None => create(createdBy, ApplicationForm(id = id))
    }
  }

  def softDelete(deletedBy: User, application: Application) {
    dbHelpers.delete(deletedBy, application.id)
  }

  def findById(auth: Authorization, id: String): Option[Application] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    isDeleted: Option[Boolean] = Some(false),
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("-created_at", Some("applications"))
  ): Seq[Application] = {
    // TODO: Auth
    DB.withConnection { implicit c =>
      BaseQuery.
        optionalIn("applications.id", ids).
        nullBoolean("applications.deleted_at", isDeleted).
        limit(limit).
        offset(offset).
        orderBy(orderBy.sql).
        as(
          io.flow.registry.v0.anorm.parsers.Application.parser().*
        )
    }
  }

}
