package controllers

import db.ApplicationsDao
import io.flow.common.v0.models.User
import io.flow.common.v0.models.json._
import io.flow.registry.v0.models.{Application, ApplicationForm}
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
    ApplicationsDao.upsert(request.user, id) match {
      case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
      case Right(org) => Ok(Json.toJson(org))
    }
  }

  def deleteById(id: String) = Identified { request =>
    withApplication(request.user, id) { org =>
      ApplicationsDao.softDelete(request.user, org)
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
