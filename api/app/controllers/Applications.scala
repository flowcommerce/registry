package controllers

import db.{ApplicationsDao, ApplicationVersionsDao}
import io.flow.common.v0.models.User
import io.flow.common.v0.models.json._
import io.flow.registry.v0.models.{Application, ApplicationForm, ApplicationPutForm, PortType}
import io.flow.registry.v0.models.json._
import io.flow.play.controllers.IdentifiedRestController
import io.flow.play.util.Validation
import io.flow.postgresql.{Authorization, OrderBy}
import play.api.mvc._
import play.api.libs.json._

class Applications @javax.inject.Inject() (
  val userTokensClient: io.flow.play.clients.UserTokensClient
) extends Controller
     with io.flow.play.controllers.IdentifiedRestController
{

  def get(
    id: Option[Seq[String]],
    port: Option[Seq[Long]],
    `type`: Option[Seq[PortType]],
    prefix: Option[String],
    q: Option[String],
    limit: Long = 25,
    offset: Long = 0,
    sort: String
  ) = Identified { request =>
    OrderBy.parse(sort) match {
      case Left(errors) => {
        UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
      }
      case Right(orderBy) => {
        Ok(
          Json.toJson(
            ApplicationsDao.findAll(
              Authorization.User(request.user.id),
              ids = optionals(id),
              portNumbers = optionals(port),
              portTypes = optionals(`type`),
              prefix = prefix,
              q = q,
              limit = limit,
              offset = offset,
              orderBy = orderBy
            )
          )
        )
      }
    }
  }

  def getVersions(
    id: Option[Seq[String]],
    application: Option[Seq[String]],
    limit: Long = 25,
    offset: Long = 0,
    sort: String
  ) = Identified { request =>
    OrderBy.parse(sort) match {
      case Left(errors) => {
        UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
      }
      case Right(orderBy) => {
        Ok(
          Json.toJson(
            ApplicationVersionsDao.findAll(
              Authorization.User(request.user.id),
              ids = optionals(id),
              applications = optionals(application),
              limit = limit,
              offset = offset,
              orderBy = orderBy
            )
          )
        )
      }
    }
  }
  
  def getById(id: String) = Identified { request =>
    withApplication(request.user, id) { org =>
      Ok(Json.toJson(org))
    }
  }

  def post() = Identified { request =>
    JsValue.sync(request.contentType, request.body) { js =>
      js.validate[ApplicationForm] match {
        case e: JsError => {
          UnprocessableEntity(Json.toJson(Validation.invalidJson(e)))
        }
        case s: JsSuccess[ApplicationForm] => {
          ApplicationsDao.create(request.user, s.get) match {
            case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
            case Right(org) => Created(Json.toJson(org))
          }
        }
      }
    }
  }

  def putById(id: String) = Identified { request =>
    JsValue.sync(request.contentType, request.body) { js =>
      js.validate[ApplicationPutForm] match {
        case e: JsError => {
          UnprocessableEntity(Json.toJson(Validation.invalidJson(e)))
        }
        case s: JsSuccess[ApplicationPutForm] => {
          ApplicationsDao.upsert(request.user, id, s.get) match {
            case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
            case Right(org) => Ok(Json.toJson(org))
          }
        }
      }
    }
  }

  def deleteById(id: String) = Identified { request =>
    withApplication(request.user, id) { org =>
      ApplicationsDao.delete(request.user, org)
      NoContent
    }
  }

  def withApplication(user: User, id: String)(
    f: Application => Result
  ) = {
    ApplicationsDao.findById(Authorization.User(user.id), id) match {
      case None => {
        Results.NotFound
      }
      case Some(application) => {
        f(application)
      }
    }
  }
}
