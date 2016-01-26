package controllers

import io.flow.registry.v0.models.{Application, ApplicationForm, PortType}
import java.util.UUID
import play.api.libs.ws._
import play.api.test._

class ApplicationsSpec extends PlaySpecification with MockClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /applications/:id deletes" in new WithServer(port=port) {
    val application = createApplication()
    val id = application.id

    identifiedClient.applications.deleteById(id)
    expectNotFound(
      identifiedClient.applications.getById(id)
    )
  }

  "PUT /applications/:id upserts application" in new WithServer(port=port) {
    val id = createTestId()
    val updated = await(identifiedClient.applications.putById(id, createApplicationPutForm()))
    await(
      identifiedClient.applications.getById(id)
    ).id must beEqualTo(id)
  }

  "POST /applications" in new WithServer(port=port) {
    val form = createApplicationForm()

    val application = await(identifiedClient.applications.post(form))
    application.id must beEqualTo(form.id)
  }

  "POST /applications w/ existing id" in new WithServer(port=port) {
    val application = createApplication()
    val form = createApplicationForm().copy(id = application.id)

    expectErrors(
      identifiedClient.applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Application with this id already exists")
    )
  }

  "POST /applications w/ invalid id" in new WithServer(port=port) {
    val form = createApplicationForm().copy(id = " a bad id ")

    expectErrors(
      identifiedClient.applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: a-bad-id")
    )
  }

  "POST /applications w/ invalid type" in new WithServer(port=port) {
    val form = createApplicationForm().copy(`type` = Seq(PortType.UNDEFINED("foo")))

    expectErrors(
      identifiedClient.applications.post(form)
    ).errors.map(_.message) must beEqualTo(
      Seq("Invalid application type. Must be one of: api, database, ui")
    )
  }

  "GET /applications/:id" in new WithServer(port=port) {
    val application = createApplication()
    await(
      identifiedClient.applications.getById(application.id)
    ) must beEqualTo(application)

    expectNotFound(
      identifiedClient.applications.getById(createTestId())
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
      identifiedClient.applications.get(id = Some(Seq(application1.id, application2.id)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient.applications.get(id = Some(Seq(createTestId())))
    ) must be(Nil)
  }

  "GET /applications by prefix" in new WithServer(port=port) {
    val prefix = UUID.randomUUID.toString.replaceAll("\\-", "")
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-2"))

    await(
      identifiedClient.applications.get(prefix = Some(prefix))
    ).map(_.id) must beEqualTo(Seq(application2.id, application1.id))

    await(
      identifiedClient.applications.get(prefix = Some(createTestId()))
    ) must be(Nil)
  }

  "GET /applications by query" in new WithServer(port=port) {
    val prefix = UUID.randomUUID.toString.replaceAll("\\-", "")
    val application1 = createApplication(createApplicationForm().copy(id = prefix + "-foo-1"))
    val application2 = createApplication(createApplicationForm().copy(id = prefix + "-foo-2"))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient.applications.get(id = Some(ids), q = Some("foo"))
    ).map(_.id) must beEqualTo(ids.reverse)

    await(
      identifiedClient.applications.get(id = Some(ids), q = Some("foo-1"))
    ).map(_.id) must beEqualTo(Seq(application1.id))

    await(
      identifiedClient.applications.get(id = Some(ids), q = Some("foo-2"))
    ).map(_.id) must beEqualTo(Seq(application2.id))

    await(
      identifiedClient.applications.get(q = Some(UUID.randomUUID.toString))
    ) must beEqualTo(Nil)
  }

  "GET /applications by port nums" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()

    await(
      identifiedClient.applications.get(port = Some(Seq(application1.ports.map(_.num).head, application2.ports.map(_.num).head)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient.applications.get(port = Some(Seq(-100)))
    ) must be(Nil)
  }

  "GET /applications by port types" in new WithServer(port=port) {
    val application1 = createApplication(createApplicationForm().copy(`type` = Seq(PortType.Ui)))
    val application2 = createApplication(createApplicationForm().copy(`type` = Seq(PortType.Api)))
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient.applications.get(id =  Some(ids), `type` = Some(Seq(PortType.Ui, PortType.Api)))
    ).map(_.id).sorted must beEqualTo(Seq(application1.id, application2.id).sorted)

    await(
      identifiedClient.applications.get(id =  Some(ids), `type` = Some(Seq(PortType.Ui)))
    ).map(_.id) must beEqualTo(Seq(application1.id))

    await(
      identifiedClient.applications.get(id =  Some(ids), `type` = Some(Seq(PortType.Database)))
    ) must be(Nil)
  }

  "GET /applications paginates" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val application3 = createApplication()
    val ids = Seq(application1.id, application2.id, application3.id)

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "created_at", limit = 2)
    ).map(_.id) must beEqualTo(Seq(application1.id, application2.id))

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2)
    ).map(_.id) must beEqualTo(Seq(application3.id))
  }

  "GET /applications sorts" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "created_at")
    ).map(_.id) must beEqualTo(ids)

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "-created_at")
    ).map(_.id) must beEqualTo(ids.reverse)
  }

  "GET /applications sorts by port" in new WithServer(port=port) {
    val application1 = createApplication()
    val application2 = createApplication()
    val ids = Seq(application1.id, application2.id)

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "port")
    ).map(_.id) must beEqualTo(ids)

    await(
      identifiedClient.applications.get(id = Some(ids), sort = "-port")
    ).map(_.id) must beEqualTo(ids.reverse)
  }

}
