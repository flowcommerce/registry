package db

import io.flow.common.v0.models.User
import io.flow.play.util.IdGenerator
import io.flow.postgresql.{Authorization, Query, OrderBy}
import io.flow.registry.v0.models.{ServiceReference, Port}
import anorm._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

case class PortForm(
  applicationId: String,
  serviceId: String,
  internal: Long,
  external: Long
)

private[db] case class InternalPort(
  id: String,
  applicationId: String,
  serviceId: String,
  internal: Long,
  external: Long
) {

  val port = Port(
    service = ServiceReference(serviceId),
    internal = internal,
    external = external
  )

}

object PortsDao {

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

  private[this] val dbHelpers = DbHelpers("ports")
  private[this] val idGenerator = IdGenerator("prt")

  def create(createdBy: User, form: PortForm): InternalPort = {
    val id = DB.withConnection { implicit c =>
      create(c, createdBy, form)
    }
    findById(Authorization.All, id).getOrElse {
      sys.error("Failed to create port")
    }
  }

  private[db] def create(
    implicit c: java.sql.Connection,
    createdBy: User,
    form: PortForm
  ): String = {
    val id = idGenerator.randomId()
    SQL(InsertQuery).on(
      'id -> id,
      'application_id -> form.applicationId,
      'service_id -> form.serviceId,
      'internal -> form.internal,
      'external -> form.external,
      'updated_by_user_id -> createdBy.id
    ).execute()
    id
  }

  def delete(implicit c: java.sql.Connection, deletedBy: User, port: InternalPort) {
    dbHelpers.delete(c, deletedBy, port.id)
  }

  def maxExternalPortNumber(): Option[Long] = {
    PortsDao.findAll(Authorization.All, orderBy = OrderBy("-ports.external"), limit = 1).map(_.external).headOption
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
    orderBy: OrderBy = OrderBy("ports.application_id, ports.external")
  ): Seq[InternalPort] = {
    DB.withConnection { implicit c =>
      findAllWithConnection(c, auth, ids, applications, services, externals, limit, offset, orderBy)
    }
  }

  private[db] def findAllWithConnection(
    implicit c: java.sql.Connection,
    auth: Authorization,
    ids: Option[Seq[String]] = None,
    applications: Option[Seq[String]] = None,
    services: Option[Seq[String]] = None,
    externals: Option[Seq[Long]] = None,
    limit: Long = 25,
    offset: Long = 0,
    orderBy: OrderBy = OrderBy("ports.application_id, ports.external")
  ): Seq[InternalPort] = {
    // TODO: Auth
    BaseQuery.
      optionalIn("ports.id", ids).
      optionalIn("ports.application_id", applications).
      optionalIn("ports.service_id", services).
      optionalIn("ports.external", externals).
      limit(limit).
      offset(offset).
      orderBy(orderBy.sql).
      as(
        parser().*
      )
  }

  def parser() = {
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
          internal = internal
        )
      }
    }
  }

}
