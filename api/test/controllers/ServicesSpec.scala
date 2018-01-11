package controllers

import io.flow.common.v0.models.ChangeType
import util.{MockClient, RegistrySpec}

class ServicesSpec extends RegistrySpec with MockClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  "DELETE /services/:id deletes" in  {
    val service = createService()
    val id = service.id

    await(
      identifiedClient(testUser).services.deleteById(id, testHeaders)
    )
    expectNotFound(
      identifiedClient(testUser).services.getById(id, testHeaders)
    )
  }

  "PUT /services/:id updates service" in  {
    val service = createService(createServiceForm().copy(defaultPort = 5000))
    service.defaultPort must be(5000)

    val updated = await(identifiedClient().services.putById(service.id, createServicePutForm().copy(defaultPort = 5001), testHeaders))
    updated.defaultPort must be(5001)
  }

  "PUT /services/:id creates service" in  {
    val id = createTestId()
    val updated = await(identifiedClient().services.putById(id, createServicePutForm(), testHeaders))
    await(
      identifiedClient().services.getById(id)
    ).id must be(id)
  }

  "POST /services" in  {
    val form = createServiceForm()

    val service = await(identifiedClient().services.post(form, testHeaders))
    service.id must be(form.id)
  }

  "POST /services w/ existing id" in  {
    val service = createService()
    val form = createServiceForm().copy(id = service.id)

    expectErrors(
      identifiedClient().services.post(form, testHeaders)
    ).genericError.messages must contain theSameElementsAs Seq("Service with this id already exists")
  }

  "POST /services w/ invalid port" in  {
    val service = createService()
    val form = createServiceForm().copy(defaultPort = 200)

    expectErrors(
      identifiedClient().services.post(form, testHeaders)
    ).genericError.messages must contain theSameElementsAs Seq("Default port must be > 1024")
  }

  "POST /services w/ invalid id" in  {
    val form = createServiceForm().copy(id = " a bad id ")

    expectErrors(
      identifiedClient().services.post(form, testHeaders)
    ).genericError.messages must contain theSameElementsAs Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: a-bad-id")
  }

  "GET /services/:id" in  {
    val service = createService()
    await(
      identifiedClient().services.getById(service.id, testHeaders)
    ) must be(service)

    expectNotFound(
      identifiedClient().services.getById(createTestId(), testHeaders)
    )
  }

  "GET /services/:id requires authorization" in  {
    expectNotAuthorized(
      anonClient.services.get()
    )

    expectNotAuthorized(
      anonClient.services.getById(createTestId(), testHeaders)
    )

    val form = createServiceForm()
    expectNotAuthorized(
      anonClient.services.post(form)
    )

    expectNotAuthorized(
      anonClient.services.putById(createTestId(), createServicePutForm(), testHeaders)
    )

    expectNotAuthorized(
      anonClient.services.deleteById(createTestId(), testHeaders)
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
    await(identifiedClient().services.putById(service.id, createServicePutForm().copy(defaultPort = 5001), testHeaders))
    await(identifiedClient().services.deleteById(service.id, testHeaders))

    val versions = await(
      identifiedClient().services.getVersions(service = Some(Seq(service.id)), requestHeaders = testHeaders)
    )
    versions.map(_.`type`) must contain theSameElementsAs(Seq(ChangeType.Insert, ChangeType.Update, ChangeType.Delete))
    versions(0).service.defaultPort must be(5000)
    versions(1).service.defaultPort must be(5001)
    versions(2).service.defaultPort must be(5001)

  }

}
