package controllers

import io.flow.registry.v0.models.{Application, ApplicationForm, Service}
import play.api.libs.ws._
import play.api.test._

class ApplicationsSpec extends PlaySpecification with MockClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /applications/:id deletes" in new WithServer(port=port) {
    val application = createApplication()
    val id = application.id

    await(
      identifiedClient().applications.deleteById(id)
    )

    expectNotFound(
      identifiedClient().applications.getById(id)
    )
  }

  "PUT /applications/:id updates application" in new WithServer(port=port) {
    val application = createApplication(createApplicationForm().copy(service = "play"))
    await(identifiedClient().applications.putById(application.id, createApplicationPutForm().copy(service = Some("nodejs"))))

    val updated = await(
      identifiedClient().applications.getById(application.id)
    )
    updated.id must beEqualTo(application.id)
    updated.ports.map(_.service.id) must beEqualTo(Seq("nodejs", "play"))
  }

  "PUT /applications/:id requires service to create an application" in new WithServer(port=port) {
    val id = createTestId()

    expectErrors(
      identifiedClient().applications.putById(id, createApplicationPutForm())
    ).errors.map(_.message) must beEqualTo(
      Seq("Must specify service when creating application")
    )
  }

  "PUT /applications/:id creates application" in new WithServer(port=port) {
    val id = createTestId()
    await(identifiedClient().applications.putById(id, createApplicationPutForm().copy(service = Some("play"))))

    val updated = await(
      identifiedClient().applications.getById(id)
    )
    updated.id must beEqualTo(id)
    updated.ports.map(_.service.id) must beEqualTo(Seq("play"))
  }

  "POST /applications" in new WithServer(port=port) {
    val form = createApplicationForm()

    val application = await(identifiedClient().applications.post(form))
    application.id must beEqualTo(form.id)
    application.dependencies must be(Nil)
  }

  "POST /applications w/ existing id" in new WithServer(port=port) {
    val application = createApplication()
    val form = createApplicationForm().copy(id = application.id)

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Application with this id already exists")
    )
  }

  "POST /applications w/ valid explicit ports" in new WithServer(port=port) {
    val external: Long = db.PortsDao.maxExternalPortNumber.getOrElse(6000l) + 3l
    val internal = 1234

    val application = createApplication(createApplicationForm().copy(external = Some(external), internal = Some(internal)))
    val appPort = application.ports.headOption.getOrElse {
      sys.error("No ports created")
    }
    appPort.external must beEqualTo(external)
    appPort.internal must beEqualTo(internal)
  }

  "POST /applications w/ invalid external port" in new WithServer(port=port) {
    val application = createApplication()
    val form = createApplicationForm().copy(external = Some(200))

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("External port must be > 1024")
    )
  }

  "POST /applications w/ invalid internal port" in new WithServer(port=port) {
    val application = createApplication()
    val form = createApplicationForm().copy(internal = Some(-200))

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Internal port must be > 0")
    )
  }

  "POST /applications w/ invalid id" in new WithServer(port=port) {
    val form = createApplicationForm().copy(id = " a bad id ")

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: a-bad-id")
    )
  }

  "POST /applications w/ invalid service" in new WithServer(port=port) {
    val form = createApplicationForm().copy(service = createUrlKey())

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Service not found")
    )
  }

  "POST /applications w/ dependencies" in new WithServer(port=port) {
    val dep1 = createApplication()
    val dep2 = createApplication()
    val form = createApplicationForm().copy(dependency = Some(Seq(dep1.id, dep2.id)))

    val application = await(identifiedClient().applications.post(form))
    application.id must beEqualTo(form.id)
    application.dependencies must beEqualTo(Seq(dep1.id, dep2.id).sorted)
  }

  "POST /applications w/ invalid dependency ID" in new WithServer(port=port) {
    val dependencyId = createTestId()
    val form = createApplicationForm().copy(dependency = Some(Seq(dependencyId)))

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq(s"Dependency[$dependencyId] references a non existing application")
    )
  }

  "POST /applications w/ self as a dependency" in new WithServer(port=port) {
    val id = createTestId()
    val form = createApplicationForm().copy(id = id, dependency = Some(Seq(id)))

    expectErrors(
      identifiedClient().applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq(s"Cannot declare dependency[$id] on self")
    )
  }

  "GET /applications/:id" in new WithServer(port=port) {
    val application = createApplication()
    await(
      identifiedClient().applications.getById(application.id)
    ) must beEqualTo(application)

    expectNotFound(
      identifiedClient().applications.getById(createTestId())
    )
  }

  "GET /applications/:id requires authorization" in new WithServer(port=port) {
    expectNotAuthorized(
      anonClient.applications.get()
    )

    expectNotAuthorized(
      anonClient.applications.getById(createTestId())
    )

    val form = createApplicationForm()
    expectNotAuthorized(
      anonClient.applications.post(form)
    )

    expectNotAuthorized(
      anonClient.applications.putById(createTestId(), createApplicationPutForm())
    )

    expectNotAuthorized(
      anonClient.applications.deleteById(createTestId())
    )
  }

  "GET /applications by ids" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()

    await(
      identifiedClient().applications.get(id = Some(Seq(application1.id, application2.id)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient().applications.get(id = Some(Seq(createTestId())))
    ) must be(Nil)
  }

  "GET /applications by prefix" in new WithServer(port=port) {
    val prefix = createUrlKey()
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-2"))

    await(
      identifiedClient().applications.get(prefix = Some(prefix))
    ).map(_.id) must beEqualTo(Seq(application2.id, application1.id))

    await(
      identifiedClient().applications.get(prefix = Some(createTestId()))
    ) must be(Nil)
  }

  "GET /applications by query" in new WithServer(port=port) {
    val prefix = createUrlKey()
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-foo-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-foo-2"))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo"))
    ).map(_.id) must beEqualTo(ids.reverse)

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo-1"))
    ).map(_.id) must beEqualTo(Seq(application1.id))

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo-2"))
    ).map(_.id) must beEqualTo(Seq(application2.id))

    await(
      identifiedClient().applications.get(q = Some(createUrlKey()))
    ) must beEqualTo(Nil)
  }

  "GET /applications by port nums" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()

    await(
      identifiedClient().applications.get(port = Some(Seq(application1.ports.map(_.external).head, application2.ports.map(_.external).head)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient().applications.get(port = Some(Seq(-100)))
    ) must be(Nil)
  }

  "GET /applications by service" in new WithServer(port=port) {
    val service1 = createService()
    val service2 = createService()

    val application1 = createApplication(createApplicationForm(service1))
    val application2 = createApplication(createApplicationForm(service2))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id =  Some(ids), service = Some(Seq(service1.id, service2.id)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient().applications.get(id =  Some(ids), service = Some(Seq(service1.id)))
    ).map(_.id) must beEqualTo(Seq(application1.id))

    await(
      identifiedClient().applications.get(id =  Some(ids), service = Some(Seq(testService.id)))
    ) must be(Nil)
  }

  "GET /applications paginates" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val application3 = createApplication()
    val ids = Seq(application1.id, application2.id, application3.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at", limit = 2)
    ).map(_.id) must beEqualTo(Seq(application1.id, application2.id))

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2)
    ).map(_.id) must beEqualTo(Seq(application3.id))
  }

  "GET /applications sorts" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at")
    ).map(_.id) must beEqualTo(ids)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "-created_at")
    ).map(_.id) must beEqualTo(ids.reverse)
  }

  "GET /applications sorts by port" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "port")
    ).map(_.id) must beEqualTo(ids)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "-port")
    ).map(_.id) must beEqualTo(ids.reverse)
  }

}
