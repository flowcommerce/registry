package db

import io.flow.common.v0.models.ChangeType
import io.flow.postgresql.Authorization
import io.flow.registry.v0.models.ApplicationPutForm
import util.RegistrySpec

class ApplicationVersionsDaoSpec extends RegistrySpec {

  "versions insert" in {
    val app = createApplication()

    val versions = applicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.map(_.application.id) must be(Seq(app.id))
    versions.map(_.`type`) must be(Seq(ChangeType.Insert))
    versions.flatMap(_.application.ports).map(_.external) must be(app.ports.map(_.external))
  }

  "versions delete" in {
    val app = createApplication()
    applicationsDao.delete(testUser, app)
    val versions = applicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.map(_.`type`) must be(Seq(ChangeType.Insert, ChangeType.Delete))
  }

  "versions port changes" in {
    val newService = createService()
    val app = createApplication()
    val updated = rightOrErrors(
      applicationsDao.update(testUser, app, ApplicationPutForm(service = Some(newService.id)))
    )

    val versions = applicationVersionsDao.findAll(Authorization.All, applications = Some(Seq(app.id)))
    versions.size must be(2)
    versions(0).application.ports.map(_.external) must be(app.ports.map(_.external))
    versions(1).application.ports.map(_.external) must be(updated.ports.map(_.external))
  }

}
