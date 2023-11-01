package db

import javax.inject.{Inject, Singleton}

import io.flow.common.v0.models.UserReference
import io.flow.util.IdGenerator
import io.flow.postgresql.{Authorization, OrderBy, Query}
import anorm._
import io.flow.postgresql.play.db.DbHelpers
import play.api.db._

case class DependencyForm(
  applicationId: String,
  dependencyId: String
)

case class InternalDependency(
  id: String,
  applicationId: String,
  dependencyId: String
)

@Singleton
class DependenciesDao @Inject() (
  db: Database
) extends lib.PublicAuthorizedQuery {

  private val dbHelpers = DbHelpers(db, "dependencies")

  private[this] val BaseQuery = Query(s"""
    select dependencies.id,
           dependencies.application_id,
           dependencies.dependency_id
      from dependencies
  """)

  private[this] val InsertQuery = """
    insert into dependencies
    (id, application_id, dependency_id, updated_by_user_id)
    values
    ({id}, {application_id}, {dependency_id}, {updated_by_user_id})
  """

  private[this] val idGenerator = IdGenerator("dep")

  def create(createdBy: UserReference, form: DependencyForm): InternalDependency = {
    val id = db.withConnection { implicit c =>
      create(c, createdBy, form)
    }
    findById(Authorization.All, id).getOrElse {
      sys.error("Failed to create dependency")
    }
  }

  private[db] def create(implicit
    c: java.sql.Connection,
    createdBy: UserReference,
    form: DependencyForm
  ): String = {
    val id = idGenerator.randomId()
    SQL(InsertQuery)
      .on(
        "id" -> id,
        "application_id" -> form.applicationId,
        "dependency_id" -> form.dependencyId,
        "updated_by_user_id" -> createdBy.id
      )
      .execute()
    id
  }

  private[db] def deleteApplicationDependency(implicit
    c: java.sql.Connection,
    user: UserReference,
    applicationId: String,
    dependencyId: String
  ): Unit = {
    findAll(
      Authorization.All,
      applications = Some(Seq(applicationId)),
      dependencies = Some(Seq(dependencyId)),
      limit = 1
    ).foreach { dep =>
      delete(c, user, dep)
    }
  }

  def delete(implicit c: java.sql.Connection, deletedBy: UserReference, dependency: InternalDependency): Unit = {
    dbHelpers.delete(c, deletedBy, dependency.id)
  }

  def findById(auth: Authorization, id: String): Option[InternalDependency] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  private[db] def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    dependencies: Option[Seq[String]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("dependencies.application_id, dependencies.dependency_id")
  ): Seq[InternalDependency] = {
    db.withConnection { implicit c =>
      findAllWithConnection(c, auth, ids, applications, dependencies, limit, offset, orderBy)
    }
  }

  private[db] def findAllWithConnection(implicit
    c: java.sql.Connection,
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    dependencies: Option[Seq[String]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("dependencies.application_id, dependencies.dependency_id")
  ): Seq[InternalDependency] = {
    dbHelpers
      .authorizedQuery(BaseQuery, queryAuth(auth))
      .optionalIn("dependencies.id", ids)
      .optionalIn("dependencies.application_id", applications)
      .optionalIn("dependencies.dependency_id", dependencies)
      .limit(limit)
      .offset(offset)
      .orderBy(orderBy.sql)
      .as(
        parser.*
      )
  }

  private[this] val parser: RowParser[InternalDependency] = {
    SqlParser.str("id") ~
      SqlParser.str("application_id") ~
      SqlParser.str("dependency_id") map {
        case id ~ applicationId ~ dependencyId => {
          InternalDependency(
            id = id,
            applicationId = applicationId,
            dependencyId = dependencyId
          )
        }
      }
  }

}
