package controllers

import io.flow.registry.v0.models.Healthcheck
import io.flow.registry.v0.models.json._

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Healthchecks extends Controller {

  private val HealthyJson = Json.toJson(Healthcheck(status = "healthy"))

  def getInternalAndHealthcheck() = Action { request =>
    Ok(HealthyJson)
  }

}
