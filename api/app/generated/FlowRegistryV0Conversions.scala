/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.1.31
 * apibuilder:0.13.0 https://app.apibuilder.io/flow/registry/0.1.31/anorm_2_x_parsers
 */
package io.flow.registry.v0.anorm.conversions {

  import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
  import play.api.libs.json.{JsArray, JsObject, JsValue}
  import scala.util.{Failure, Success, Try}

  /**
    * Conversions to collections of objects using JSON.
    */
  object Util {

    def parser[T](
      f: play.api.libs.json.JsValue => T
    ) = anorm.Column.nonNull { (value, meta) =>
      val MetaDataItem(columnName, nullable, clazz) = meta
      value match {
        case json: org.postgresql.util.PGobject => parseJson(f, columnName.qualified, json.getValue)
        case json: java.lang.String => parseJson(f, columnName.qualified, json)
        case _=> {
          Left(
            TypeDoesNotMatch(
              s"Column[${columnName.qualified}] error converting $value to Json. Expected instance of type[org.postgresql.util.PGobject] and not[${value.asInstanceOf[AnyRef].getClass}]"
            )
          )
        }


      }
    }

    private[this] def parseJson[T](f: play.api.libs.json.JsValue => T, columnName: String, value: String) = {
      Try {
        f(
          play.api.libs.json.Json.parse(value)
        )
      } match {
        case Success(result) => Right(result)
        case Failure(ex) => Left(
          TypeDoesNotMatch(
            s"Column[$columnName] error parsing json $value: $ex"
          )
        )
      }
    }

  }

  object Types {
    import io.flow.registry.v0.models.json._
    implicit val columnToSeqRegistryApplication: Column[Seq[_root_.io.flow.registry.v0.models.Application]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Application]] }
    implicit val columnToMapRegistryApplication: Column[Map[String, _root_.io.flow.registry.v0.models.Application]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Application]] }
    implicit val columnToSeqRegistryApplicationForm: Column[Seq[_root_.io.flow.registry.v0.models.ApplicationForm]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ApplicationForm]] }
    implicit val columnToMapRegistryApplicationForm: Column[Map[String, _root_.io.flow.registry.v0.models.ApplicationForm]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ApplicationForm]] }
    implicit val columnToSeqRegistryApplicationPutForm: Column[Seq[_root_.io.flow.registry.v0.models.ApplicationPutForm]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ApplicationPutForm]] }
    implicit val columnToMapRegistryApplicationPutForm: Column[Map[String, _root_.io.flow.registry.v0.models.ApplicationPutForm]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ApplicationPutForm]] }
    implicit val columnToSeqRegistryApplicationVersion: Column[Seq[_root_.io.flow.registry.v0.models.ApplicationVersion]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ApplicationVersion]] }
    implicit val columnToMapRegistryApplicationVersion: Column[Map[String, _root_.io.flow.registry.v0.models.ApplicationVersion]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ApplicationVersion]] }
    implicit val columnToSeqRegistryHttp: Column[Seq[_root_.io.flow.registry.v0.models.Http]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Http]] }
    implicit val columnToMapRegistryHttp: Column[Map[String, _root_.io.flow.registry.v0.models.Http]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Http]] }
    implicit val columnToSeqRegistryPort: Column[Seq[_root_.io.flow.registry.v0.models.Port]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Port]] }
    implicit val columnToMapRegistryPort: Column[Map[String, _root_.io.flow.registry.v0.models.Port]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Port]] }
    implicit val columnToSeqRegistryPostgresql: Column[Seq[_root_.io.flow.registry.v0.models.Postgresql]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Postgresql]] }
    implicit val columnToMapRegistryPostgresql: Column[Map[String, _root_.io.flow.registry.v0.models.Postgresql]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Postgresql]] }
    implicit val columnToSeqRegistryService: Column[Seq[_root_.io.flow.registry.v0.models.Service]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Service]] }
    implicit val columnToMapRegistryService: Column[Map[String, _root_.io.flow.registry.v0.models.Service]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Service]] }
    implicit val columnToSeqRegistryServiceForm: Column[Seq[_root_.io.flow.registry.v0.models.ServiceForm]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ServiceForm]] }
    implicit val columnToMapRegistryServiceForm: Column[Map[String, _root_.io.flow.registry.v0.models.ServiceForm]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ServiceForm]] }
    implicit val columnToSeqRegistryServicePutForm: Column[Seq[_root_.io.flow.registry.v0.models.ServicePutForm]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ServicePutForm]] }
    implicit val columnToMapRegistryServicePutForm: Column[Map[String, _root_.io.flow.registry.v0.models.ServicePutForm]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ServicePutForm]] }
    implicit val columnToSeqRegistryServiceReference: Column[Seq[_root_.io.flow.registry.v0.models.ServiceReference]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ServiceReference]] }
    implicit val columnToMapRegistryServiceReference: Column[Map[String, _root_.io.flow.registry.v0.models.ServiceReference]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ServiceReference]] }
    implicit val columnToSeqRegistryServiceVersion: Column[Seq[_root_.io.flow.registry.v0.models.ServiceVersion]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.ServiceVersion]] }
    implicit val columnToMapRegistryServiceVersion: Column[Map[String, _root_.io.flow.registry.v0.models.ServiceVersion]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.ServiceVersion]] }
    implicit val columnToSeqRegistryHealthcheck: Column[Seq[_root_.io.flow.registry.v0.models.Healthcheck]] = Util.parser { _.as[Seq[_root_.io.flow.registry.v0.models.Healthcheck]] }
    implicit val columnToMapRegistryHealthcheck: Column[Map[String, _root_.io.flow.registry.v0.models.Healthcheck]] = Util.parser { _.as[Map[String, _root_.io.flow.registry.v0.models.Healthcheck]] }
  }

  object Standard {
    implicit val columnToJsObject: Column[play.api.libs.json.JsObject] = Util.parser { _.as[play.api.libs.json.JsObject] }
    implicit val columnToSeqBoolean: Column[Seq[Boolean]] = Util.parser { _.as[Seq[Boolean]] }
    implicit val columnToMapBoolean: Column[Map[String, Boolean]] = Util.parser { _.as[Map[String, Boolean]] }
    implicit val columnToSeqDouble: Column[Seq[Double]] = Util.parser { _.as[Seq[Double]] }
    implicit val columnToMapDouble: Column[Map[String, Double]] = Util.parser { _.as[Map[String, Double]] }
    implicit val columnToSeqInt: Column[Seq[Int]] = Util.parser { _.as[Seq[Int]] }
    implicit val columnToMapInt: Column[Map[String, Int]] = Util.parser { _.as[Map[String, Int]] }
    implicit val columnToSeqLong: Column[Seq[Long]] = Util.parser { _.as[Seq[Long]] }
    implicit val columnToMapLong: Column[Map[String, Long]] = Util.parser { _.as[Map[String, Long]] }
    implicit val columnToSeqLocalDate: Column[Seq[_root_.org.joda.time.LocalDate]] = Util.parser { _.as[Seq[_root_.org.joda.time.LocalDate]] }
    implicit val columnToMapLocalDate: Column[Map[String, _root_.org.joda.time.LocalDate]] = Util.parser { _.as[Map[String, _root_.org.joda.time.LocalDate]] }
    implicit val columnToSeqDateTime: Column[Seq[_root_.org.joda.time.DateTime]] = Util.parser { _.as[Seq[_root_.org.joda.time.DateTime]] }
    implicit val columnToMapDateTime: Column[Map[String, _root_.org.joda.time.DateTime]] = Util.parser { _.as[Map[String, _root_.org.joda.time.DateTime]] }
    implicit val columnToSeqBigDecimal: Column[Seq[BigDecimal]] = Util.parser { _.as[Seq[BigDecimal]] }
    implicit val columnToMapBigDecimal: Column[Map[String, BigDecimal]] = Util.parser { _.as[Map[String, BigDecimal]] }
    implicit val columnToSeqJsObject: Column[Seq[_root_.play.api.libs.json.JsObject]] = Util.parser { _.as[Seq[_root_.play.api.libs.json.JsObject]] }
    implicit val columnToMapJsObject: Column[Map[String, _root_.play.api.libs.json.JsObject]] = Util.parser { _.as[Map[String, _root_.play.api.libs.json.JsObject]] }
    implicit val columnToSeqJsValue: Column[Seq[_root_.play.api.libs.json.JsValue]] = Util.parser { _.as[Seq[_root_.play.api.libs.json.JsValue]] }
    implicit val columnToMapJsValue: Column[Map[String, _root_.play.api.libs.json.JsValue]] = Util.parser { _.as[Map[String, _root_.play.api.libs.json.JsValue]] }
    implicit val columnToSeqString: Column[Seq[String]] = Util.parser { _.as[Seq[String]] }
    implicit val columnToMapString: Column[Map[String, String]] = Util.parser { _.as[Map[String, String]] }
    implicit val columnToSeqUUID: Column[Seq[_root_.java.util.UUID]] = Util.parser { _.as[Seq[_root_.java.util.UUID]] }
    implicit val columnToMapUUID: Column[Map[String, _root_.java.util.UUID]] = Util.parser { _.as[Map[String, _root_.java.util.UUID]] }
  }

}