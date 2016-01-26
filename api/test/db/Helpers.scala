package db

import io.flow.common.v0.models.User
import io.flow.postgresql.Authorization
import io.flow.registry.v0.models._
import io.flow.play.clients.MockUserTokensClient
import io.flow.play.util.{IdGenerator, UrlKey}
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
    // Note that id cannot have dashes as we assume dashes as
    // separators when trying to find a common base name of the
    // application during port allocation
    ApplicationForm(
      id = UUID.randomUUID.toString.replaceAll("\\-", ""),
      `type` = Seq(PortType.Api)
    )
  }

  def createApplicationPutForm(): ApplicationPutForm = {
    ApplicationPutForm(
      `type` = Seq(PortType.Api)
    )
  }

  def createPort(
    form: PortForm = createPortForm()
  ) (
    implicit user: User = testUser
  ): InternalPort = {
    rightOrErrors(PortsDao.create(user, form))
  }

  def createPortForm(
    application: Application = createApplication()
  ): PortForm = {
    val num: Long  = PortsDao.maxPortNumber.getOrElse(6000)

    PortForm(
      applicationId = application.id,
      typ = PortType.Api,
      num = num + 1
    )
  }

}
