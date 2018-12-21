package controllers

import io.flow.common.v0.models.ChangeType
import util.{MockRegistryClient, RegistrySpec}

class ServicesSpec extends RegistrySpec with MockRegistryClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /services/:id deletes" in  {
    val service = createService()
    val id = service.id

    println(s"TEST CASE 1")
    await(
      identifiedClient().services.deleteById(id)
    )

    println(s"TEST CASE 2")
    expectNotFound(
      identifiedClient().services.deleteById(id)
    )

    println(s"TEST CASE 3")
    expectNotFound(
      identifiedClient().services.getById(id)
    )
  }

  "PUT /services/:id updates service" in  {
    val service = createService(createServiceForm().copy(defaultPort = 5000))
    service.defaultPort must be(5000)

    val updated = await(identifiedClient(user = testUser).services.putById(service.id, createServicePutForm().copy(defaultPort = 5001)))
    updated.defaultPort must be(5001)
  }

  "PUT /services/:id creates service" in  {
    val id = createTestId()
    val updated = await(identifiedClient().services.putById(id, createServicePutForm()))
    await(
      identifiedClient().services.getById(id)
    ).id must be(id)
  }

  "POST /services" in  {
    val form = createServiceForm()

    val service = await(identifiedClient().services.post(form))
    service.id must be(form.id)
  }

  "POST /services w/ existing id" in  {
    val service = createService()
    val form = createServiceForm().copy(id = service.id)

    expectErrors(
      identifiedClient().services.post(form)
    ).genericError.messages must contain theSameElementsAs Seq("Service with this id already exists")
  }

  "POST /services w/ invalid port" in  {
    val service = createService()
    val form = createServiceForm().copy(defaultPort = 200)

    expectErrors(
      identifiedClient().services.post(form)
    ).genericError.messages must contain theSameElementsAs Seq("Default port must be > 1024")
  }

  "POST /services w/ invalid id" in  {
    val form = createServiceForm().copy(id = " a bad id ")

    expectErrors(
      identifiedClient().services.post(form)
    ).genericError.messages must contain theSameElementsAs Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: a-bad-id")
  }

  "GET /services/:id" in  {
    val service = createService()
    await(
      identifiedClient().services.getById(service.id)
    ) must be(service)

    expectNotFound(
      identifiedClient().services.getById(createTestId())
    )
  }

  "GET /services/:id requires authorization" in  {
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

  "GET /services by ids" in  {
    val service1 = createService()
    val service2 = createService()

    await(
      identifiedClient().services.get(id = Some(Seq(service1.id, service2.id)), requestHeaders = testHeaders)
    ).map(_.id).sorted must contain theSameElementsAs(Seq(service1.id, service2.id).sorted)

    await(
      identifiedClient().services.get(id = Some(Seq(createTestId())), requestHeaders = testHeaders)
    ) must be(Nil)
  }


  "GET /services paginates" in  {
    val service1 = createService()
    val service2 = createService()
    val service3 = createService()
    val ids = Seq(service1.id, service2.id, service3.id)

    await(
      identifiedClient().services.get(id = Some(ids), sort = "created_at", limit = 2)
    ).map(_.id) must contain theSameElementsAs(Seq(service1.id, service2.id))

    await(
      identifiedClient().services.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2)
    ).map(_.id) must contain theSameElementsAs(Seq(service3.id))
  }

  "GET /services sorts" in  {
    val service1 = createService()
    val service2 = createService()
    val ids = Seq(service1.id, service2.id)

    await(
      identifiedClient().services.get(id = Some(ids), sort = "created_at")
    ).map(_.id) must contain theSameElementsAs(ids)

    await(
      identifiedClient().services.get(id = Some(ids), sort = "-created_at")
    ).map(_.id) must contain theSameElementsAs(ids.reverse)
  }

  "GET /services/versions" in  {
    val service = createService(createServiceForm().copy(defaultPort = 5000))
    await(identifiedClient().services.putById(service.id, createServicePutForm().copy(defaultPort = 5001)))
    await(identifiedClient().services.deleteById(service.id))

    val versions = await(
      identifiedClient().services.getVersions(service = Some(Seq(service.id)), requestHeaders = testHeaders)
    )
    versions.map(_.`type`) must contain theSameElementsAs(Seq(ChangeType.Insert, ChangeType.Update, ChangeType.Delete))
    versions(0).service.defaultPort must be(5000)
    versions(1).service.defaultPort must be(5001)
    versions(2).service.defaultPort must be(5001)

  }

}
