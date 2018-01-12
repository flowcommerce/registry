package controllers

import db.{ApplicationsDao, ServiceVersionsDao, ServicesDao}
import io.flow.common.v0.models.UserReference
import io.flow.error.v0.models.json._
import io.flow.registry.v0.models.{Service, ServiceForm, ServicePutForm}
import io.flow.registry.v0.models.json._
import io.flow.play.controllers.{FlowController, FlowControllerComponents}
import io.flow.play.util.{Config, Validation}
import io.flow.postgresql.{Authorization, OrderBy}
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.Future

class Services @javax.inject.Inject() (
      servicesDao: ServicesDao,
      serviceVersionsDao: ServiceVersionsDao,
      applicationsDao: ApplicationsDao,
      val config: Config,
      val controllerComponents: ControllerComponents,
      val flowControllerComponents: FlowControllerComponents
  ) extends FlowController {

  import scala.concurrent.ExecutionContext.Implicits.global

  def get(
    id: Option[Seq[String]],
    limit: Long = 25,
    offset: Long = 0,
    sort: String
  ) = Anonymous.async { request =>
    Future {
      OrderBy.parse(sort) match {
        case Left(errors) => {
          UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
        }
        case Right(orderBy) => {
          Ok(
            Json.toJson(
              servicesDao.findAll(
                Authorization.fromUser(request.user.map(_.id)),
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
    Future {
      OrderBy.parse(sort) match {
        case Left(errors) => {
          UnprocessableEntity(Json.toJson(Validation.invalidSort(errors)))
        }
        case Right(orderBy) => {
          Ok(
            Json.toJson(
              serviceVersionsDao.findAll(
                Authorization.fromUser(request.user.map(_.id)),
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
    withService(request.user, id) { service =>
      Ok(Json.toJson(service))
    }
  }

  def post() = Identified.async(parse.json) { request =>
    Future {
      servicesDao.create(request.user, request.body.as[ServiceForm]) match {
        case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
        case Right(service) => Created(Json.toJson(service))
      }
    }
  }

  def putById(id: String) = Identified.async(parse.json) { request =>
    Future {
      val form = ServiceForm(id = id, defaultPort = request.body.as[ServicePutForm].defaultPort)
      servicesDao.findById(Authorization.User(request.user.id), id) match {
        case None => {
          servicesDao.create(request.user, form) match {
            case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
            case Right(service) => Created(Json.toJson(service))
          }
        }
        case Some(service) => {
          servicesDao.update(request.user, service, form) match {
            case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
            case Right(service) => Ok(Json.toJson(service))
          }
        }
      }
    }
  }

  def deleteById(id: String) = Identified.async { request =>
    withService(Some(request.user), id) { service =>
      applicationsDao.findAll(Authorization.All, services = Some(Seq(service.id)), limit = 1) match {
        case Nil => {
          servicesDao.delete(request.user, service)
            NoContent
        }
        case apps => {
          UnprocessableEntity(Json.toJson(Validation.error("1 or more applications is using this service")))
        }
      }
    }
  }

  def withService(user: Option[UserReference], id: String)(
    f: Service => Result
  ) = {
    Future {
      servicesDao.findById(Authorization.fromUser(user.map(_.id)), id) match {
        case None => {
          Results.NotFound
        }
        case Some(service) => {
          f(service)
        }
      }
    }
  }
}
