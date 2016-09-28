package controllers

import io.flow.common.v0.models.ChangeType
import io.flow.registry.v0.models.{Service, ServiceForm}
import play.api.libs.ws._
import play.api.test._

class ServicesSpec extends PlaySpecification with MockClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /services/:id deletes" in new WithServer(port=port) {
    val service = createService()
    val id = service.id

    await(
      jwtClient().services.deleteById(id)
    )
    expectNotFound(
      jwtClient().services.getById(id)
    )
  }

  "PUT /services/:id updates service" in new WithServer(port=port) {
    val service = createService(createServiceForm().copy(defaultPort = 5000))
    service.defaultPort must beEqualTo(5000)

    val updated = await(jwtClient().services.putById(service.id, createServicePutForm().copy(defaultPort = 5001)))
    updated.defaultPort must beEqualTo(5001)
  }

  "PUT /services/:id creates service" in new WithServer(port=port) {
    val id = createTestId()
    val updated = await(jwtClient().services.putById(id, createServicePutForm()))
    await(
      jwtClient().services.getById(id)
    ).id must beEqualTo(id)
  }

  "POST /services" in new WithServer(port=port) {
    val form = createServiceForm()

    val service = await(jwtClient().services.post(form))
    service.id must beEqualTo(form.id)
  }

  "POST /services w/ existing id" in new WithServer(port=port) {
    val service = createService()
    val form = createServiceForm().copy(id = service.id)

    expectErrors(
      jwtClient().services.post(form)
    ).genericError.messages must beEqualTo(
      Seq("Service with this id already exists")
    )
  }

  "POST /services w/ invalid port" in new WithServer(port=port) {
    val service = createService()
    val form = createServiceForm().copy(defaultPort = 200)

    expectErrors(
      jwtClient().services.post(form)
    ).genericError.messages must beEqualTo(
      Seq("Default port must be > 1024")
    )
  }

  "POST /services w/ invalid id" in new WithServer(port=port) {
    val form = createServiceForm().copy(id = " a bad id ")

    expectErrors(
      jwtClient().services.post(form)
    ).genericError.messages must beEqualTo(
      Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: a-bad-id")
    )
  }

  "GET /services/:id" in new WithServer(port=port) {
    val service = createService()
    await(
      jwtClient().services.getById(service.id)
    ) must beEqualTo(service)

    expectNotFound(
      jwtClient().services.getById(createTestId())
    )
  }

  "GET /services/:id requires authorization" in new WithServer(port=port) {
    expectNotAuthorized(
      anonClient.services.get()
    )

    expectNotAuthorized(
      anonClient.services.getById(createTestId())
    )

    val form = createServiceForm()
    expectNotAuthorized(
      anonClient.services.post(form)
    )

    expectNotAuthorized(
      anonClient.services.putById(createTestId(), createServicePutForm())
    )

    expectNotAuthorized(
      anonClient.services.deleteById(createTestId())
    )
  }

  "GET /services by ids" in new WithServer(port=port) {
    val service1 = createService()
    val service2 = createService()

    await(
      jwtClient().services.get(id = Some(Seq(service1.id, service2.id)))
    ).map(_.id).sorted must beEqualTo(Seq(service1.id, service2.id).sorted)

    await(
      jwtClient().services.get(id = Some(Seq(createTestId())))
    ) must be(Nil)
  }


  "GET /services paginates" in new WithServer(port=port) {
    val service1 = createService()
    val service2 = createService()
    val service3 = createService()
    val ids = Seq(service1.id, service2.id, service3.id)

    await(
      jwtClient().services.get(id = Some(ids), sort = "created_at", limit = 2)
    ).map(_.id) must beEqualTo(Seq(service1.id, service2.id))

    await(
      jwtClient().services.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2)
    ).map(_.id) must beEqualTo(Seq(service3.id))
  }

  "GET /services sorts" in new WithServer(port=port) {
    val service1 = createService()
    val service2 = createService()
    val ids = Seq(service1.id, service2.id)

    await(
      jwtClient().services.get(id = Some(ids), sort = "created_at")
    ).map(_.id) must beEqualTo(ids)

    await(
      jwtClient().services.get(id = Some(ids), sort = "-created_at")
    ).map(_.id) must beEqualTo(ids.reverse)
  }

  "GET /services/versions" in new WithServer(port=port) {
    val service = createService(createServiceForm().copy(defaultPort = 5000))
    val updated = await(jwtClient().services.putById(service.id, createServicePutForm().copy(defaultPort = 5001)))
    await(jwtClient().services.deleteById(service.id))

    val versions = await(
      jwtClient().services.getVersions(service = Some(Seq(service.id)))
    )
    versions.map(_.`type`) must beEqualTo(Seq(ChangeType.Insert, ChangeType.Update, ChangeType.Delete))
    versions(0).service.defaultPort must beEqualTo(5000)
    versions(1).service.defaultPort must beEqualTo(5001)
    versions(2).service.defaultPort must beEqualTo(5001)

  }

}
