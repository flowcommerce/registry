package db

import javax.inject.{Inject, Singleton}
import anorm._
import io.flow.common.v0.models.UserReference
import io.flow.util.IdGenerator
import io.flow.postgresql.play.db.DbHelpers
import io.flow.postgresql.{Authorization, OrderBy, Query}
import io.flow.registry.v0.models.{Port, ServiceReference}
import play.api.db._

case class PortForm(
  applicationId: String,
  serviceId: String,
  internal: Long,
  external: Long,
)

case class InternalPort(
  id: String,
  applicationId: String,
  serviceId: String,
  internal: Long,
  external: Long,
) {

  val port = Port(
    service = ServiceReference(serviceId),
    internal = internal,
    external = external,
  )

}

@Singleton
class PortsDao @Inject() (
  db: Database,
) extends lib.PublicAuthorizedQuery {

  private val dbHelpers = DbHelpers(db, "ports")

  private[this] val BaseQuery = Query(s"""
    select ports.id,
           ports.application_id,
           ports.service_id,
           ports.external,
           ports.internal
      from ports
  """)

  private[this] val InsertQuery = """
    insert into ports
    (id, application_id, service_id, internal, external, updated_by_user_id)
    values
    ({id}, {application_id}, {service_id}, {internal}, {external}, {updated_by_user_id})
  """

  private[this] val idGenerator = IdGenerator("prt")

  def create(createdBy: UserReference, form: PortForm): InternalPort = {
    val id = db.withConnection { implicit c =>
      create(c, createdBy, form)
    }
    findById(Authorization.All, id).getOrElse {
      sys.error("Failed to create port")
    }
  }

  private[db] def create(implicit
    c: java.sql.Connection,
    createdBy: UserReference,
    form: PortForm,
  ): String = {
    val id = idGenerator.randomId()
    SQL(InsertQuery)
      .on(
        "id" -> id,
        "application_id" -> form.applicationId,
        "service_id" -> form.serviceId,
        "internal" -> form.internal,
        "external" -> form.external,
        "updated_by_user_id" -> createdBy.id,
      )
      .execute()
    id
  }

  def delete(implicit c: java.sql.Connection, deletedBy: UserReference, port: InternalPort): Unit = {
    dbHelpers.delete(c, deletedBy, port.id)
  }

  def maxExternalPortNumber(): Option[Long] = {
    findAll(Authorization.All, orderBy = OrderBy("-ports.external"), limit = 1).map(_.external).headOption
  }

  def findByExternal(auth: Authorization, external: Long): Option[InternalPort] = {
    findAll(auth, externals = Some(Seq(external)), limit = 1).headOption
  }

  def findById(auth: Authorization, id: String): Option[InternalPort] = {
    findAll(auth, ids = Some(Seq(id)), limit = 1).headOption
  }

  private[db] def findAll(
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    services: Option[Seq[String]] = None,
    externals: Option[Seq[Long]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("ports.application_id, ports.external"),
  ): Seq[InternalPort] = {
    db.withConnection { implicit c =>
      findAllWithConnection(c, auth, ids, applications, services, externals, limit, offset, orderBy)
    }
  }

  private[db] def findAllWithConnection(implicit
    c: java.sql.Connection,
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    services: Option[Seq[String]] = None,
    externals: Option[Seq[Long]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("ports.application_id, ports.external"),
  ): Seq[InternalPort] = {
    dbHelpers
      .authorizedQuery(BaseQuery, queryAuth(auth))
      .optionalIn("ports.id", ids)
      .optionalIn("ports.application_id", applications)
      .optionalIn("ports.service_id", services)
      .optionalIn("ports.external", externals)
      .limit(limit)
      .offset(offset)
      .orderBy(orderBy.sql)
      .as(
        parser.*,
      )
  }

  private[this] val parser: RowParser[InternalPort] = {
    SqlParser.str("id") ~
      SqlParser.str("application_id") ~
      SqlParser.str("service_id") ~
      SqlParser.long("external") ~
      SqlParser.long("internal") map {
        case id ~ applicationId ~ serviceId ~ external ~ internal => {
          InternalPort(
            id = id,
            applicationId = applicationId,
            serviceId = serviceId,
            external = external,
            internal = internal,
          )
        }
      }
  }

}
