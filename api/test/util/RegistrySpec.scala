package util

import db._
import io.flow.common.v0.models.UserReference
import io.flow.postgresql.Authorization
import io.flow.registry.api.lib.DefaultPortAllocator
import io.flow.registry.v0.models._
import io.flow.test.utils.FlowPlaySpec

trait RegistrySpec extends FlowPlaySpec {
  lazy val portsDao: PortsDao = init[PortsDao]
  lazy val servicesDao: ServicesDao = init[ServicesDao]
  lazy val dependenciesDao: DependenciesDao = init[DependenciesDao]
  lazy val applicationsDao: ApplicationsDao = init[ApplicationsDao]
  lazy val applicationVersionsDao: ApplicationVersionsDao = init[ApplicationVersionsDao]
  lazy val defaultPortAllocator: DefaultPortAllocator = init[DefaultPortAllocator]

  def testService: Service = servicesDao.findById(Authorization.All, "test").getOrElse {
    rightOrErrors(servicesDao.create(testUser, createServiceForm()))
  }

  def createService(form: ServiceForm = createServiceForm())(implicit user: UserReference = testUser): Service = {
    rightOrErrors(servicesDao.create(user, form))
  }

  def createServiceForm(): ServiceForm = {
    ServiceForm(
      id = createUrlKey("svc"),
      defaultPort = 8080
    )
  }

  def createServicePutForm(): ServicePutForm = {
    ServicePutForm(
      defaultPort = 8080
    )
  }

  def createApplication(
    form: ApplicationForm = createApplicationForm()
  )(implicit user: UserReference = testUser): io.flow.registry.v0.models.Application = {
    rightOrErrors(applicationsDao.create(user, form))
  }

  def createApplicationForm(
    service: Service = testService
  ): ApplicationForm = {
    ApplicationForm(
      id = createUrlKey("app"),
      service = service.id
    )
  }

  def createApplicationPutForm(): ApplicationPutForm = {
    ApplicationPutForm()
  }

  def createPort(
    form: PortForm = createPortForm()
  )(implicit
    user: UserReference = testUser
  ): InternalPort = {
    portsDao.create(user, form)
  }

  def createPortForm(
    application: io.flow.registry.v0.models.Application = createApplication()
  ): PortForm = {
    lazy val nextPort: Long = portsDao.maxExternalPortNumber().getOrElse(6000)

    PortForm(
      applicationId = application.id,
      serviceId = testService.id,
      internal = testService.defaultPort,
      external = nextPort + 1
    )
  }

  def createDependency(
    form: DependencyForm = createDependencyForm()
  )(implicit
    user: UserReference = testUser
  ): InternalDependency = {
    dependenciesDao.create(user, form)
  }

  def createDependencyForm(
    application: io.flow.registry.v0.models.Application = createApplication(),
    dependency: io.flow.registry.v0.models.Application = createApplication()
  ): DependencyForm = {
    DependencyForm(
      applicationId = application.id,
      dependencyId = dependency.id
    )
  }
}
