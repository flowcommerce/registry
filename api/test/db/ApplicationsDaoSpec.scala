package db

import io.flow.postgresql.Authorization
import io.flow.registry.v0.models.{Application, ApplicationPutForm}
import util.RegistrySpec

class ApplicationsDaoSpec extends RegistrySpec {

  def validatePort(modulus: Int, app: Application) {
    app.ports.size must be(1)
    app.ports.foreach { p =>
      if (p.external % 10 != modulus) {
        fail(s"Application port[${p.external}] for service[${p.service.id}] must end in ${modulus}")
      }
    }
  }

  "respects application service when allocating ports" in {
    val base = createUrlKey()

    validatePort(
      0,
      createApplication(
        createApplicationForm().copy(
          id = base + "-ui",
          service = "nodejs"
        )
      )
    )

    validatePort(
      1,
      createApplication(
        createApplicationForm().copy(
          id = base + "-api",
          service = "play"
        )
      )
    )

    validatePort(
      9,
      createApplication(
        createApplicationForm().copy(
          id = base + "-db",
          service = "postgresql"
        )
      )
    )


  }

  "allocates ports based on type" in {
    val base = createUrlKey()

    val ui = createApplication(createApplicationForm().copy(id = base + "-ui", service = "nodejs"))
    val api = createApplication(createApplicationForm().copy(id = base + "-api", service = "play"))
    val postgresql = createApplication(createApplicationForm().copy(id = base + "-db", service = "postgresql"))

    val uiPort = ui.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    (uiPort % 10) must be(0)
    api.ports.map(_.external) must be(Seq(uiPort + 1))
    postgresql.ports.map(_.external) must be(Seq(uiPort + 9))
  }

  "allocates block ranges in sets of 10" in {
    val base = createUrlKey()

    val api = createApplication(createApplicationForm().copy(id = base, service = "play"))

    // inject the other app here so we have to 'jump' over its allocation
    val other = createApplication(createApplicationForm().copy(id = createUrlKey(), service = "play"))

    val api2 = createApplication(createApplicationForm().copy(id = base + "-api2", service = "play"))

    val apiPort = api.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val otherPort = other.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port for other app")
    }

    val api2Port = api2.ports.map(_.external).headOption.getOrElse {
      sys.error("Failed to allocate port")
    }

    val offset = otherPort - apiPort + 10
    (offset % 10) must be(0)

    Seq(apiPort + offset, apiPort + offset + 10).contains(api2Port) must be(true)
  }

  "deleting an application also deletes its ports" in {
    val app = createApplication()
    val portNumber = app.ports.map(_.external).headOption.getOrElse {
      sys.error("No port for application")
    }
    portsDao.findByExternal(Authorization.All, portNumber).getOrElse {
      sys.error("Could not find port")
    }

    applicationsDao.delete(testUser, app)
    applicationsDao.findById(Authorization.All, app.id) must be(None)
    portsDao.findByExternal(Authorization.All, portNumber) must be(None)
  }

  "deleting an application also deletes its dependencies" in {
    val app = createApplication()
    val app2 = createApplication(createApplicationForm().copy(dependency = Some(Seq(app.id))))

    applicationsDao.delete(testUser, app2)
    applicationsDao.findById(Authorization.All, app2.id) must be(None)    
  }

  "update does not modify dependencies if not provided" in {
    val app = createApplication()
    val app2 = createApplication(createApplicationForm().copy(dependency = Some(Seq(app.id))))

    val updated = rightOrErrors(applicationsDao.update(testUser, app2, ApplicationPutForm()))
    updated.id must be(app2.id)
    updated.dependencies must be(Seq(app.id))
  }

  "update replaces dependencies if provided" in {
    val app1 = createApplication()
    val app2 = createApplication()

    val myApp = createApplication(createApplicationForm().copy(dependency = Some(Seq(app1.id))))

    val updated = rightOrErrors(applicationsDao.update(testUser, myApp, ApplicationPutForm(dependency = Some(Seq(app2.id)))))
    updated.dependencies must be(Seq(app2.id))
  }

  "update allocates new ports" in {
    val app = createApplication(createApplicationForm().copy(service = "nodejs"))
    val portNumber = app.ports.map(_.external).headOption.getOrElse {
      sys.error("No port for application")
    }
    app.ports.map(_.external) must be(Seq(portNumber))

    val updated = rightOrErrors(
      applicationsDao.update(
        testUser,
        app,
        ApplicationPutForm(service = Some("play"))
      )
    )
    updated.ports.map(_.external) must be(Seq(portNumber, portNumber + 1))

    // Now test idempotence
    val updatedAgain = rightOrErrors(
      applicationsDao.update(
        testUser,
        updated,
        ApplicationPutForm(service = Some("play"))
      )
    )
    updatedAgain.ports.map(_.external) must be(Seq(portNumber, portNumber + 1))
  }

  "can reuse ID once deleted" in {
    val app = createApplication()
    applicationsDao.delete(testUser, app)
    val app2 = createApplication(createApplicationForm().copy(id = app.id))
  }

  "validates that there are no circular dependencies" in {
    val base = createApplication()
    val other = createApplication(createApplicationForm().copy(dependency = Some(Seq(base.id))))
    val form = createApplicationPutForm().copy(dependency = Some(Seq(other.id)))

    applicationsDao.update(testUser, base, form).left.get must be(
      Seq(s"Application[${base.id}] Cannot declare a circular dependency on[${other.id}]")
    )

  }

}
