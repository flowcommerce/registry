package db

import io.flow.postgresql.Authorization
import util.RegistrySpec

class DependenciesDaoSpec extends RegistrySpec {

  "create" in {
    val form = createDependencyForm()
    val dependency = createDependency(form)
    dependency.applicationId must be(form.applicationId)
    dependency.dependencyId must be(form.dependencyId)
  }

  "findById" in {
    val dependency = createDependency()
    dependenciesDao.findById(Authorization.All, dependency.id).map(_.id) must be(
      Some(dependency.id)
    )

    dependenciesDao.findById(Authorization.All, createTestId()) must be(None)
  }

  "findAll" must {

    "filter by ids" in {
      val dependency1 = createDependency()
      val dependency2 = createDependency()

      dependenciesDao.findAll(Authorization.All, ids = Some(Seq(dependency1.id, dependency2.id))).map(_.id).sorted must be(
        Seq(dependency1.id, dependency2.id).sorted
      )

      dependenciesDao.findAll(Authorization.All, ids = Some(Nil)) must be(Nil)
      dependenciesDao.findAll(Authorization.All, ids = Some(Seq(createTestId()))) must be(Nil)
      dependenciesDao.findAll(Authorization.All, ids = Some(Seq(dependency1.id, createTestId()))).map(_.id) must be(Seq(dependency1.id))
    }

    "filter by applications" in {
      val dependency1 = createDependency()
      val dependency2 = createDependency()

      dependenciesDao.findAll(Authorization.All, applications = Some(Seq(dependency1.applicationId, dependency2.applicationId))).map(_.id).sorted must be(
        Seq(dependency1.id, dependency2.id).sorted
      )

      dependenciesDao.findAll(Authorization.All, applications = Option(Nil)) must be(Nil)
      dependenciesDao.findAll(Authorization.All, applications = Option(Seq(createTestId()))) must be(Nil)
      dependenciesDao.findAll(Authorization.All, applications = Option(Seq(dependency1.applicationId, createTestId()))).map(_.id) must be(Seq(dependency1.id))
    }

  }

}
