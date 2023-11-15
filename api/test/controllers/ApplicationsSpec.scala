package controllers

import util.{MockRegistryClient, RegistrySpec}

class ApplicationsSpec extends RegistrySpec with MockRegistryClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /applications/:id deletes" in {
    val application = createApplication()
    val id = application.id

    await(
      identifiedClient().applications.deleteById(id),
    )

    expectNotFound(
      identifiedClient().applications.getById(id),
    )
  }

  "PUT /applications/:id updates application" in {
    val application = createApplication(createApplicationForm().copy(service = "play"))
    await(
      identifiedClient().applications.putById(
        application.id,
        createApplicationPutForm().copy(service = Some("nodejs")),
        testHeaders,
      ),
    )

    val updated = await(
      identifiedClient().applications.getById(application.id),
    )
    updated.id must be(application.id)
    updated.ports.map(_.service.id) must contain theSameElementsAs Seq("nodejs", "play")
  }

  "PUT /applications/:id requires service to create an application" in {
    val id = createTestId()

    expectErrors(
      identifiedClient().applications.putById(id, createApplicationPutForm()),
    ).genericError.messages must contain theSameElementsAs Seq("Must specify service when creating application")

  }

  "PUT /applications/:id creates application" in {
    val id = createTestId()
    await(identifiedClient().applications.putById(id, createApplicationPutForm().copy(service = Some("play"))))

    val updated = await(
      identifiedClient().applications.getById(id),
    )
    updated.id must be(id)
    updated.ports.map(_.service.id) must contain theSameElementsAs Seq("play")
  }

  "POST /applications" in {
    val form = createApplicationForm()

    val application = await(identifiedClient().applications.post(form))
    application.id must be(form.id)
    application.dependencies must be(Nil)
  }

  "POST /applications w/ existing id" in {
    val application = createApplication()
    val form = createApplicationForm().copy(id = application.id)

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq("Application with this id already exists")
  }

  "POST /applications w/ valid explicit ports" in {
    val external: Long = portsDao.maxExternalPortNumber().getOrElse(6000L) + 3L
    val internal = 1234L

    val application =
      createApplication(createApplicationForm().copy(external = Some(external), internal = Some(internal)))
    val appPort = application.ports.headOption.getOrElse {
      sys.error("No ports created")
    }
    appPort.external must be(external)
    appPort.internal must be(internal)
  }

  "POST /applications w/ invalid external port" in {
    val form = createApplicationForm().copy(external = Some(200))

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq("External port must be > 1024")
  }

  "POST /applications w/ invalid internal port" in {
    val form = createApplicationForm().copy(internal = Some(-200))

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq("Internal port must be > 0")
  }

  "POST /applications w/ invalid id" in {
    val form = createApplicationForm().copy(id = " a bad id ")

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq(
      "Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: abadid",
    )
  }

  "POST /applications w/ invalid service" in {
    val form = createApplicationForm().copy(service = createUrlKey())

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq("Service not found")
  }

  "POST /applications w/ dependencies" in {
    val dep1 = createApplication()
    val dep2 = createApplication()
    val form = createApplicationForm().copy(dependency = Some(Seq(dep1.id, dep2.id)))

    val application = await(identifiedClient().applications.post(form))
    application.id must be(form.id)
    application.dependencies must contain theSameElementsAs Seq(dep1.id, dep2.id).sorted
  }

  "POST /applications w/ invalid dependency ID" in {
    val dependencyId = createTestId()
    val form = createApplicationForm().copy(dependency = Some(Seq(dependencyId)))

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq(
      s"Dependency[$dependencyId] references a non existing application",
    )
  }

  "POST /applications w/ self as a dependency" in {
    val id = createTestId()
    val form = createApplicationForm().copy(id = id, dependency = Some(Seq(id)))

    expectErrors(
      identifiedClient().applications.post(form),
    ).genericError.messages must contain theSameElementsAs Seq(s"Cannot declare dependency[$id] on self")
  }

  "GET /applications/:id" in {
    val application = createApplication()
    await(
      identifiedClient().applications.getById(application.id),
    ) must be(application)

    expectNotFound(
      identifiedClient().applications.getById(createTestId()),
    )
  }

  "GET /applications/:id is anonymous" in {
    val app = createApplication()
    await {
      anonClient.applications.get(id = Some(Seq(app.id)))
    }.map(_.id) must equal(Seq(app.id))
  }

  "creating an application requires authorization" in {
    val form = createApplicationForm()
    expectNotAuthorized(
      anonClient.applications.post(form),
    )

    expectNotAuthorized(
      anonClient.applications.putById(createTestId(), createApplicationPutForm()),
    )

    expectNotAuthorized(
      anonClient.applications.deleteById(createTestId()),
    )
  }

  "GET /applications by ids" in {
    val application1 = createApplication()
    val application2 = createApplication()

    await(
      identifiedClient().applications.get(id = Some(Seq(application1.id, application2.id))),
    ).map(_.id).sorted must contain theSameElementsAs Seq(application1.id, application2.id).sorted

    await(
      identifiedClient().applications.get(id = Some(Seq(createTestId()))),
    ) must be(Nil)
  }

  "GET /applications by prefix" in {
    val prefix = createUrlKey()
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-2"))

    await(
      identifiedClient().applications.get(prefix = Some(prefix)),
    ).map(_.id) must contain theSameElementsAs Seq(application2.id, application1.id)

    await(
      identifiedClient().applications.get(prefix = Some(createTestId())),
    ) must be(Nil)
  }

  "GET /applications by query" in {
    val prefix = createUrlKey()
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-foo-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-foo-2"))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo")),
    ).map(_.id) must be(ids.reverse)

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo-1")),
    ).map(_.id) must contain theSameElementsAs Seq(application1.id)

    await(
      identifiedClient().applications.get(id = Some(ids), q = Some("foo-2")),
    ).map(_.id) must contain theSameElementsAs Seq(application2.id)

    await(
      identifiedClient().applications.get(q = Some(createUrlKey())),
    ) must be(Nil)
  }

  "GET /applications by port nums" in {
    val application1 = createApplication()
    val application2 = createApplication()

    await(
      identifiedClient().applications.get(port =
        Some(Seq(application1.ports.map(_.external).head, application2.ports.map(_.external).head)),
      ),
    ).map(_.id).sorted must contain theSameElementsAs Seq(application1.id, application2.id).sorted

    await(
      identifiedClient().applications.get(port = Some(Seq(-100))),
    ) must be(Nil)
  }

  "GET /applications by service" in {
    val service1 = createService()
    val service2 = createService()

    val application1 = createApplication(createApplicationForm(service1))
    val application2 = createApplication(createApplicationForm(service2))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), service = Some(Seq(service1.id, service2.id))),
    ).map(_.id).sorted must contain theSameElementsAs Seq(application1.id, application2.id).sorted

    await(
      identifiedClient().applications.get(id = Some(ids), service = Some(Seq(service1.id))),
    ).map(_.id) must contain theSameElementsAs Seq(application1.id)

    await(
      identifiedClient().applications.get(id = Some(ids), service = Some(Seq(testService.id))),
    ) must be(Nil)
  }

  "GET /applications paginates" in {
    val application1 = createApplication()
    val application2 = createApplication()
    val application3 = createApplication()
    val ids = Seq(application1.id, application2.id, application3.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at", limit = 2),
    ).map(_.id) must contain theSameElementsAs Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2),
    ).map(_.id) must contain theSameElementsAs Seq(application3.id)
  }

  "GET /applications sorts" in {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "created_at"),
    ).map(_.id) must contain theSameElementsAs ids

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "-created_at"),
    ).map(_.id) must contain theSameElementsAs ids.reverse
  }

  "GET /applications sorts by port" in {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "port"),
    ).map(_.id) must contain theSameElementsAs ids

    await(
      identifiedClient().applications.get(id = Some(ids), sort = "-port"),
    ).map(_.id) must contain theSameElementsAs ids.reverse
  }

  "PUT /applications/:id/dependencies/:dependency" in {
    val dep1 = createApplication()
    val dep2 = createApplication()
    val application = createApplication()

    await(
      identifiedClient().applications.putDependenciesByIdAndDependency(application.id, dep1.id),
    ).dependencies must contain theSameElementsAs Seq(dep1.id)

    val finalApp = await(identifiedClient().applications.putDependenciesByIdAndDependency(application.id, dep2.id))
    finalApp.id must be(application.id)
    finalApp.ports must contain theSameElementsAs application.ports
    finalApp.dependencies.sorted must contain theSameElementsAs Seq(dep1.id, dep2.id).sorted

    expectErrors(
      identifiedClient().applications.putDependenciesByIdAndDependency(application.id, "other"),
    ).genericError.messages must contain theSameElementsAs Seq("Application named[other] not found")
  }

  "DELETE /applications/:id/dependencies/:dependency" in {
    val dep1 = createApplication()
    val dep2 = createApplication()
    val form = createApplicationForm().copy(dependency = Some(Seq(dep1.id, dep2.id)))
    val application = createApplication(form)

    await(
      identifiedClient().applications.deleteDependenciesByIdAndDependency(application.id, dep2.id),
    ).dependencies must contain theSameElementsAs Seq(dep1.id)

    // Duplicate okay
    await(
      identifiedClient().applications.deleteDependenciesByIdAndDependency(application.id, dep2.id),
    ).dependencies must contain theSameElementsAs Seq(dep1.id)

    val finalApp = await(identifiedClient().applications.deleteDependenciesByIdAndDependency(application.id, dep1.id))
    finalApp.id must be(application.id)
    finalApp.ports must contain theSameElementsAs application.ports
    finalApp.dependencies must be(Nil)

    expectErrors(
      identifiedClient().applications.deleteDependenciesByIdAndDependency(application.id, "other"),
    ).genericError.messages must contain theSameElementsAs Seq("Application named[other] not found")
  }

}
