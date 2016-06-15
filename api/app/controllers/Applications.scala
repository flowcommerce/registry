package controllers

import db.{ApplicationVersionsDao, ApplicationsDao}
import io.flow.common.v0.models.UserReference
import io.flow.common.v0.models.json._
import io.flow.registry.v0.models.{Application, ApplicationForm, ApplicationPutForm}
import io.flow.registry.v0.models.json._
import io.flow.play.util.Validation
import io.flow.postgresql.{Authorization, OrderBy}
import play.api.mvc._
import play.api.libs.json._
import net.jcazevedo.moultingyaml._

class Applications @javax.inject.Inject() (
  val tokenClient: io.flow.token.v0.interfaces.Client
) extends Controller
     with io.flow.play.controllers.IdentifiedRestController
{

  def get(
    id: Option[Seq[String]],
    port: Option[Seq[Long]],
    service: Option[Seq[String]],
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
              services = optionals(service),
              portNumbers = optionals(port),
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

  def getYaml() = Identified { request =>

    val apps = ApplicationsDao.findAll(
      Authorization.User(request.user.id)
    )

    val yaml = apps.map { a =>

      //build up port array
      val ports = a.ports.map{p =>

        val healthcheck = p.service.id match {
          case "play" | "nodejs" =>
            YamlObject(
              YamlString("  host") -> YamlString("vm"),
              YamlString("  port") -> YamlNumber(p.external)
            )

          case "postgresql" =>
            YamlObject(
              YamlString("  db_name") -> YamlString(s"${a.id}-postgresql"),
              YamlString("  host") -> YamlString("vm"),
              YamlString("  port") -> YamlNumber(p.external),
              YamlString("  user") -> YamlString("api")
            )
        }

        YamlObject(
          YamlString("healthcheck") -> healthcheck,
          YamlString("  external") -> YamlNumber(p.external),
          YamlString("  internal") -> YamlNumber(p.internal))}

      val portYaml = YamlArray(ports.toVector)

      //build dependencies
      val dependencies = a.dependencies.mkString(", ")

      //merge together with id for main application object
      YamlObject(YamlString(a.id) -> YamlObject(
        YamlString("ports") -> portYaml,
        YamlString("dependencies") -> YamlString(s"[$dependencies]")
      ))
    }

    Ok(
      YamlArray(yaml.toVector).prettyPrint.
        replaceAll("- healthcheck", "  - healthcheck").
        replaceAll("'", "")
    )
  }
  
  def getById(id: String) = Identified { request =>
    withApplication(request.user, id) { app =>
      Ok(Json.toJson(app))
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
            case Right(app) => Created(Json.toJson(app))
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
          val putForm = s.get
          ApplicationsDao.findById(Authorization.User(request.user.id), id) match {
            case None => {
              putForm.service match {
                case None => {
                  UnprocessableEntity(Json.toJson(Validation.error("Must specify service when creating application")))
                }
                case Some(service) => {
                  val form = ApplicationForm(
                    id = id,
                    service = service,
                    external = putForm.external,
                    internal = putForm.internal,
                    dependency = putForm.dependency
                  )
                  ApplicationsDao.create(request.user, form) match {
                    case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
                    case Right(application) => Created(Json.toJson(application))
                  }
                }
              }
            }
            case Some(application) => {
              ApplicationsDao.update(request.user, application, putForm) match {
                case Left(errors) => UnprocessableEntity(Json.toJson(Validation.errors(errors)))
                case Right(application) => Ok(Json.toJson(application))
              }
            }
          }
        }
      }
    }
  }

  def deleteById(id: String) = Identified { request =>
    withApplication(request.user, id) { app =>
      ApplicationsDao.delete(request.user, app)
      NoContent
    }
  }

  def withApplication(user: UserReference, id: String)(
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
