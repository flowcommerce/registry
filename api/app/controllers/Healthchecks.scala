package controllers

import db.HealthchecksDao
import io.flow.healthcheck.v0.models.Healthcheck
import io.flow.healthcheck.v0.models.json._
import io.flow.error.v0.models.json._
import io.flow.play.controllers.{FlowController, FlowControllerComponents}
import io.flow.play.util.Validation
import play.api._
import play.api.mvc._
import play.api.libs.json._
import io.flow.play.util.Config

@javax.inject.Singleton
class Healthchecks @javax.inject.Inject()(
     healthchecksDao: HealthchecksDao,
     val config: Config,
     val controllerComponents: ControllerComponents,
     val flowControllerComponents: FlowControllerComponents
   ) extends FlowController {

  private[this] val HealthyJson = Json.toJson(Healthcheck(status = "healthy"))

  def getHealthcheck() = Action { request =>
    val checks = Map(
      "db" -> healthchecksDao.isHealthy()
    )

    checks.filter { case (name, check) => !check }.keys.toList match {
      case Nil => Ok(HealthyJson)
      case unhealthy => UnprocessableEntity(Json.toJson(Validation.errors(unhealthy.map { name => s"$name failed check" })))
    }
  }

}
