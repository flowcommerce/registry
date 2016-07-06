package controllers

import db.{ApplicationsDao, ServicesDao, ServiceVersionsDao}
import io.flow.common.v0.models.UserReference
import io.flow.common.v0.models.json._
import io.flow.registry.v0.models.{Service, ServiceForm, ServicePutForm}
import io.flow.registry.v0.models.json._
import io.flow.play.controllers.IdentifiedRestController
import io.flow.play.util.Validation
import io.flow.postgresql.{Authorization, OrderBy}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.Future

class Services @javax.inject.Inject() (
  val tokenClient: io.flow.token.v0.interfaces.Client
) extends Controller
    with io.flow.play.controllers.IdentifiedRestController
{

  import scala.concurrent.ExecutionContext.Implicits.global

  def get(
    id: Option[Seq[String]],
    limit: Long = 25,
    offset: Long = 0,
    sort: String
  ) = Anonymous.async { request =>
    OrderBy.parse(sort) match {
      case Left(errors) => Future {
        UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
      }
      case Right(orderBy) => {
        request.user.map { user =>
          Ok(
            Json.toJson(
              ServicesDao.findAll(
                Authorization.fromUser(user.map(_.id)),
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
  }

  def getVersions(
    id: Option[Seq[String]],
    service: Option[Seq[String]],
    limit: Long = 25,
    offset: Long = 0,
    sort: String
  ) = Anonymous.async { request =>
    OrderBy.parse(sort) match {
      case Left(errors) => Future {
        UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
      }
      case Right(orderBy) => {
        request.user.map { user =>
          Ok(
            Json.toJson(
              ServiceVersionsDao.findAll(
                Authorization.fromUser(user.map(_.id)),
                ids = optionals(id),
                services = optionals(service),
                limit = limit,
                offset = offset,
                orderBy = orderBy
              )
            )
          )
        }
      }
    }
  }
  
  def getById(id: String) = Anonymous.async { request =>
    request.user.map { user =>
      withService(user, id) { service =>
        Ok(Json.toJson(service))
      }
    }
  }

  def post() = Identified.async { request =>
    Future {
      JsValue.sync(request.contentType, request.body) { js =>
        js.validate[ServiceForm] match {
          case e: JsError => {
            UnprocessableEntity(Json.toJson(Validation.invalidJson(e)))
          }
          case s: JsSuccess[ServiceForm] => {
            ServicesDao.create(request.user, s.get) match {
              case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
              case Right(service) => Created(Json.toJson(service))
            }
          }
        }
      }
    }
  }

  def putById(id: String) = Identified.async { request =>
    Future {
      JsValue.sync(request.contentType, request.body) { js =>
        js.validate[ServicePutForm] match {
          case e: JsError => {
            UnprocessableEntity(Json.toJson(Validation.invalidJson(e)))
          }
          case s: JsSuccess[ServicePutForm] => {
            val putForm = s.get
            val form = ServiceForm(id = id, defaultPort = putForm.defaultPort)
            ServicesDao.findById(Authorization.User(request.user.id), id) match {
              case None => {
                ServicesDao.create(request.user, form) match {
                  case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
                  case Right(service) => Created(Json.toJson(service))
                }
              }
              case Some(service) => {
                ServicesDao.update(request.user, service, form) match {
                  case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
                  case Right(service) => Ok(Json.toJson(service))
                }
              }
            }
          }
        }
      }
    }
  }

  def deleteById(id: String) = Identified.async { request =>
    Future {
      withService(Some(request.user), id) { service =>
        ApplicationsDao.findAll(Authorization.All, services = Some(Seq(service.id)), limit = 1) match {
          case Nil => {
            ServicesDao.delete(request.user, service)
            NoContent
          }
          case apps => {
            UnprocessableEntity(Json.toJson(Validation.error("1 or more applications is using this service")))
          }
        }
      }
    }
  }

  def withService(user: Option[UserReference], id: String)(
    f: Service => Result
  ) = {
    ServicesDao.findById(Authorization.fromUser(user.map(_.id)), id) match {
      case None => {
        Results.NotFound
      }
      case Some(service) => {
        f(service)
      }
    }
  }
}
