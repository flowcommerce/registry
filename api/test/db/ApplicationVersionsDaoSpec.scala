package db

import io.flow.common.v0.models.ChangeType
import io.flow.registry.v0.models.{ApplicationPutForm, PortType}
import io.flow.postgresql.Authorization
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

class ApplicationVersionsDaoSpec extends PlaySpec with OneAppPerSuite with Helpers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "versions insert" in {
    val app = createApplication()

    val versions = ApplicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.map(_.application.id) must be(Seq(app.id))
    versions.map(_.`type`) must be(Seq(ChangeType.Insert))
    versions.flatMap(_.application.ports).map(_.num) must be(app.ports.map(_.num))
  }

  "versions delete" in {
    val app = createApplication()
    ApplicationsDao.delete(testUser, app)
    val versions = ApplicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.map(_.`type`) must be(Seq(ChangeType.Insert, ChangeType.Delete))
  }

  "versions port changes" in {
    val app = createApplication()
    val updated = rightOrErrors(
      ApplicationsDao.upsert(testUser, app.id, ApplicationPutForm(`type` = Seq(PortType.Api)))
    )

    val versions = ApplicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.size must be(2)
    versions(0).application.ports.map(_.num) must be(app.ports.map(_.num))
    versions(1).application.ports.map(_.num) must be(updated.ports.map(_.num))
  }

}
