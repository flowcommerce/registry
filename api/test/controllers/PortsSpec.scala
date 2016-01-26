package controllers

import io.flow.registry.v0.models.{Port, PortForm}

import play.api.libs.ws._
import play.api.test._

class PortsSpec extends PlaySpecification with MockClient {

  import scala.concurrent.ExecutionContext.Implicits.global


  "DELETE /ports/:number deletes" in new WithServer(port=port) {
    val number = createPort().number

    identifiedClient.ports.deleteByNumber(number)
    expectNotFound(
      identifiedClient.ports.getByNumber(number)
    )
  }

  "POST /ports" in new WithServer(port=port) {
    val form = createPortForm()

    val p = await(identifiedClient.ports.post(form))
    p.application.id must beEqualTo(form.applicationId)
    p.number must beEqualTo(form.number)
  }

  "GET /ports/:number" in new WithServer(port=port) {
    val p = createPort()
    await(
      identifiedClient.ports.getByNumber(p.number)
    ) must beEqualTo(p)

    expectNotFound(
      identifiedClient.ports.getByNumber(-100)
    )
  }

  "GET /ports/:number requires authorization" in new WithServer(port=port) {
    expectNotAuthorized(
      anonClient.ports.get()
    )

    expectNotAuthorized(
      anonClient.ports.getByNumber(100)
    )

    val form = createPortForm()
    expectNotAuthorized(
      anonClient.ports.post(form)
    )

    expectNotAuthorized(
      anonClient.ports.deleteByNumber(100)
    )
  }


  "GET /ports by ids" in new WithServer(port=port) {
    val port1 = createPort()
    val port2 = createPort()

    await(
      identifiedClient.ports.get(id = Some(Seq(port1.id, port2.id)))
    ).map(_.id).sorted must beEqualTo(Seq(port1.id, port2.id).sorted)

    await(
      identifiedClient.ports.get(id = Some(Seq(createTestId())))
    ) must be(Nil)
  }

  "GET /ports by application ids" in new WithServer(port=port) {
    val port1 = createPort()
    val port2 = createPort()

    await(
      identifiedClient.ports.get(
        id = Some(Seq(port1.id, port2.id)),
        application = Some(Seq(port1.application.id, port2.application.id))
      )
    ).map(_.id).sorted must beEqualTo(Seq(port1.id, port2.id).sorted)

    await(
      identifiedClient.ports.get(application = Some(Seq(createTestId())))
    ) must be(Nil)
  }

  "GET /ports by numbers" in new WithServer(port=port) {
    val port1 = createPort()
    val port2 = createPort()

    await(
      identifiedClient.ports.get(number = Some(Seq(port1.number, port2.number)))
    ).map(_.id).sorted must beEqualTo(Seq(port1.id, port2.id).sorted)

    await(
      identifiedClient.ports.get(number = Some(Seq(-100)))
    ) must be(Nil)
  }

  "GET /ports paginates" in new WithServer(port=port) {
    val port1 = createPort()
    val port2 = createPort()
    val port3 = createPort()
    val ids = Seq(port1.id, port2.id, port3.id)

    await(
      identifiedClient.ports.get(id = Some(ids), sort = "created_at", limit = 2)
    ).map(_.id) must beEqualTo(Seq(port1.id, port2.id))

    await(
      identifiedClient.ports.get(id = Some(ids), sort = "created_at", limit = 2, offset = 2)
    ).map(_.id) must beEqualTo(Seq(port3.id))
  }

  "GET /ports sorts" in new WithServer(port=port) {
    val port1 = createPort()
    val port2 = createPort()
    val ids = Seq(port1.id, port2.id)

    await(
      identifiedClient.ports.get(id = Some(ids), sort = "created_at")
    ).map(_.id) must beEqualTo(ids)

    await(
      identifiedClient.ports.get(id = Some(ids), sort = "-created_at")
    ).map(_.id) must beEqualTo(ids.reverse)

  }

}
