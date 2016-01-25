package db

import io.flow.common.v0.models.User
import io.flow.postgresql.Authorization
import io.flow.registry.v0.models._
import io.flow.play.clients.MockUserTokensClient
import io.flow.play.util.IdGenerator
import io.flow.postgresql.OrderBy
import java.util.UUID

trait Helpers {

  lazy val testUser = createUser()
  val idGenerator = IdGenerator("tst")

  def createTestId(): String = {
    idGenerator.randomId()
  }

  def createTestName(): String = {
    s"Z Test ${UUID.randomUUID}"
  }

  def createTestEmail(): String = {
    createTestId() + "@test.flow.io"
  }

  def createUser(): User = {
    MockUserTokensClient.makeUser()
  }

  def rightOrErrors[T](result: Either[Seq[String], T]): T = {
    result match {
      case Left(errors) => sys.error(errors.mkString(", "))
      case Right(obj) => obj
    }
  }

  def createApplication(
    form: ApplicationForm = createApplicationForm()
  ) (
    implicit user: User = testUser
  ): Application = {
    rightOrErrors(ApplicationsDao.create(user, form))
  }

  def createApplicationForm(): ApplicationForm = {
    ApplicationForm(
      id = createTestId()
    )
  }

  def createPort(
    form: PortForm = createPortForm()
  ) (
    implicit user: User = testUser
  ): Port = {
    rightOrErrors(PortsDao.create(user, form))
  }

  def createPortForm(
    application: Application = createApplication()
  ): PortForm = {
    // Find a unique port number...
    val number = PortsDao.findAll(Authorization.All, orderBy = OrderBy("-ports.number"), limit = 1).headOption match {
      case None => 2000
      case Some(p) => p.number + 1
    }

    PortForm(
      applicationId = application.id,
      number = number
    )
  }

}
