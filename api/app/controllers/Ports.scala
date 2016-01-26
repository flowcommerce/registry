package controllers

import db.PortsDao
import io.flow.common.v0.models.User
import io.flow.common.v0.models.json._
import io.flow.registry.v0.models.{Port, PortForm}
import io.flow.registry.v0.models.json._
import io.flow.play.controllers.IdentifiedRestController
import io.flow.play.util.Validation
import io.flow.postgresql.{Authorization, OrderBy}
import play.api.mvc._
import play.api.libs.json._

class Ports @javax.inject.Inject() (
  val userTokensClient: io.flow.play.clients.UserTokensClient
) extends Controller
     with io.flow.play.controllers.IdentifiedRestController
{

  def get(
    id: Option[Seq[String]],
    number: Option[Seq[Long]],
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
            PortsDao.findAll(
              Authorization.User(request.user.id),
              ids = optionals(id),
              numbers = optionals(number),
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

  def getByNumber(number: Long) = Identified { request =>
    withPort(request.user, number) { org =>
      Ok(Json.toJson(org))
    }
  }

  def post() = Identified { request =>
    JsValue.sync(request.contentType, request.body) { js =>
      js.validate[PortForm] match {
        case e: JsError => {
          UnprocessableEntity(Json.toJson(Validation.invalidJson(e)))
        }
        case s: JsSuccess[PortForm] => {
          PortsDao.create(request.user, s.get) match {
            case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
            case Right(org) => Created(Json.toJson(org))
          }
        }
      }
    }
  }

  def deleteByNumber(number: Long) = Identified { request =>
    withPort(request.user, number) { org =>
      PortsDao.softDelete(request.user, org)
      NoContent
    }
  }

  def withPort(user: User, number: Long)(
    f: Port => Result
  ) = {
    PortsDao.findByNumber(Authorization.User(user.id), number) match {
      case None => {
        Results.NotFound
      }
      case Some(port) => {
        f(port)
      }
    }
  }
}
