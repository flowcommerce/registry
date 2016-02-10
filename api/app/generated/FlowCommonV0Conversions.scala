/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.9
 * apidoc:0.11.8 http://www.apidoc.me/flow/common/0.0.9/anorm_2_x_parsers
 */
package io.flow.common.v0.anorm.conversions {

  import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
  import play.api.libs.json.{JsArray, JsObject, JsValue}
  import scala.util.{Failure, Success, Try}

  /**
    * Conversions to collections of objects using JSON.
    */
  object Json {

    def parser[T](
      f: play.api.libs.json.JsValue => T
    ) = anorm.Column.nonNull1 { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case json: org.postgresql.util.PGobject => {
          Try {
            f(
              play.api.libs.json.Json.parse(
                json.getValue
              )
            )
          } match {
            case Success(result) => Right(result)
            case Failure(ex) => Left(
              TypeDoesNotMatch(
                s"Column[$qualified] error parsing json $value: $ex"
              )
            )
          }
        }
        case _=> {
          Left(
            TypeDoesNotMatch(
              s"Column[$qualified] error converting $value: ${value.asInstanceOf[AnyRef].getClass} to Json"
            )
          )
        }


      }
    }

    implicit val columnToJsObject: Column[play.api.libs.json.JsObject] = parser { _.as[play.api.libs.json.JsObject] }

    implicit val columnToSeqBoolean: Column[Seq[Boolean]] = parser { _.as[Seq[Boolean]] }
    implicit val columnToMapBoolean: Column[Map[String, Boolean]] = parser { _.as[Map[String, Boolean]] }
    implicit val columnToSeqDouble: Column[Seq[Double]] = parser { _.as[Seq[Double]] }
    implicit val columnToMapDouble: Column[Map[String, Double]] = parser { _.as[Map[String, Double]] }
    implicit val columnToSeqInt: Column[Seq[Int]] = parser { _.as[Seq[Int]] }
    implicit val columnToMapInt: Column[Map[String, Int]] = parser { _.as[Map[String, Int]] }
    implicit val columnToSeqLong: Column[Seq[Long]] = parser { _.as[Seq[Long]] }
    implicit val columnToMapLong: Column[Map[String, Long]] = parser { _.as[Map[String, Long]] }
    implicit val columnToSeqLocalDate: Column[Seq[_root_.org.joda.time.LocalDate]] = parser { _.as[Seq[_root_.org.joda.time.LocalDate]] }
    implicit val columnToMapLocalDate: Column[Map[String, _root_.org.joda.time.LocalDate]] = parser { _.as[Map[String, _root_.org.joda.time.LocalDate]] }
    implicit val columnToSeqDateTime: Column[Seq[_root_.org.joda.time.DateTime]] = parser { _.as[Seq[_root_.org.joda.time.DateTime]] }
    implicit val columnToMapDateTime: Column[Map[String, _root_.org.joda.time.DateTime]] = parser { _.as[Map[String, _root_.org.joda.time.DateTime]] }
    implicit val columnToSeqBigDecimal: Column[Seq[BigDecimal]] = parser { _.as[Seq[BigDecimal]] }
    implicit val columnToMapBigDecimal: Column[Map[String, BigDecimal]] = parser { _.as[Map[String, BigDecimal]] }
    implicit val columnToSeqJsObject: Column[Seq[_root_.play.api.libs.json.JsObject]] = parser { _.as[Seq[_root_.play.api.libs.json.JsObject]] }
    implicit val columnToMapJsObject: Column[Map[String, _root_.play.api.libs.json.JsObject]] = parser { _.as[Map[String, _root_.play.api.libs.json.JsObject]] }
    implicit val columnToSeqString: Column[Seq[String]] = parser { _.as[Seq[String]] }
    implicit val columnToMapString: Column[Map[String, String]] = parser { _.as[Map[String, String]] }
    implicit val columnToSeqUUID: Column[Seq[_root_.java.util.UUID]] = parser { _.as[Seq[_root_.java.util.UUID]] }
    implicit val columnToMapUUID: Column[Map[String, _root_.java.util.UUID]] = parser { _.as[Map[String, _root_.java.util.UUID]] }

    import io.flow.common.v0.models.json._
    implicit val columnToSeqCommonCalendar: Column[Seq[_root_.io.flow.common.v0.models.Calendar]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Calendar]] }
    implicit val columnToMapCommonCalendar: Column[Map[String, _root_.io.flow.common.v0.models.Calendar]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Calendar]] }
    implicit val columnToSeqCommonCapability: Column[Seq[_root_.io.flow.common.v0.models.Capability]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Capability]] }
    implicit val columnToMapCommonCapability: Column[Map[String, _root_.io.flow.common.v0.models.Capability]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Capability]] }
    implicit val columnToSeqCommonChangeType: Column[Seq[_root_.io.flow.common.v0.models.ChangeType]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ChangeType]] }
    implicit val columnToMapCommonChangeType: Column[Map[String, _root_.io.flow.common.v0.models.ChangeType]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ChangeType]] }
    implicit val columnToSeqCommonCountry: Column[Seq[_root_.io.flow.common.v0.models.Country]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Country]] }
    implicit val columnToMapCommonCountry: Column[Map[String, _root_.io.flow.common.v0.models.Country]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Country]] }
    implicit val columnToSeqCommonCurrency: Column[Seq[_root_.io.flow.common.v0.models.Currency]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Currency]] }
    implicit val columnToMapCommonCurrency: Column[Map[String, _root_.io.flow.common.v0.models.Currency]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Currency]] }
    implicit val columnToSeqCommonLanguage: Column[Seq[_root_.io.flow.common.v0.models.Language]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Language]] }
    implicit val columnToMapCommonLanguage: Column[Map[String, _root_.io.flow.common.v0.models.Language]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Language]] }
    implicit val columnToSeqCommonScheduleExceptionStatus: Column[Seq[_root_.io.flow.common.v0.models.ScheduleExceptionStatus]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ScheduleExceptionStatus]] }
    implicit val columnToMapCommonScheduleExceptionStatus: Column[Map[String, _root_.io.flow.common.v0.models.ScheduleExceptionStatus]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ScheduleExceptionStatus]] }
    implicit val columnToSeqCommonUnitOfMeasurement: Column[Seq[_root_.io.flow.common.v0.models.UnitOfMeasurement]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.UnitOfMeasurement]] }
    implicit val columnToMapCommonUnitOfMeasurement: Column[Map[String, _root_.io.flow.common.v0.models.UnitOfMeasurement]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.UnitOfMeasurement]] }
    implicit val columnToSeqCommonUnitOfTime: Column[Seq[_root_.io.flow.common.v0.models.UnitOfTime]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.UnitOfTime]] }
    implicit val columnToMapCommonUnitOfTime: Column[Map[String, _root_.io.flow.common.v0.models.UnitOfTime]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.UnitOfTime]] }
    implicit val columnToSeqCommonValueAddedService: Column[Seq[_root_.io.flow.common.v0.models.ValueAddedService]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ValueAddedService]] }
    implicit val columnToMapCommonValueAddedService: Column[Map[String, _root_.io.flow.common.v0.models.ValueAddedService]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ValueAddedService]] }
    implicit val columnToSeqCommonVisibility: Column[Seq[_root_.io.flow.common.v0.models.Visibility]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Visibility]] }
    implicit val columnToMapCommonVisibility: Column[Map[String, _root_.io.flow.common.v0.models.Visibility]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Visibility]] }
    implicit val columnToSeqCommonAddress: Column[Seq[_root_.io.flow.common.v0.models.Address]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Address]] }
    implicit val columnToMapCommonAddress: Column[Map[String, _root_.io.flow.common.v0.models.Address]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Address]] }
    implicit val columnToSeqCommonChangeHeader: Column[Seq[_root_.io.flow.common.v0.models.ChangeHeader]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ChangeHeader]] }
    implicit val columnToMapCommonChangeHeader: Column[Map[String, _root_.io.flow.common.v0.models.ChangeHeader]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ChangeHeader]] }
    implicit val columnToSeqCommonContact: Column[Seq[_root_.io.flow.common.v0.models.Contact]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Contact]] }
    implicit val columnToMapCommonContact: Column[Map[String, _root_.io.flow.common.v0.models.Contact]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Contact]] }
    implicit val columnToSeqCommonDatetimeRange: Column[Seq[_root_.io.flow.common.v0.models.DatetimeRange]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.DatetimeRange]] }
    implicit val columnToMapCommonDatetimeRange: Column[Map[String, _root_.io.flow.common.v0.models.DatetimeRange]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.DatetimeRange]] }
    implicit val columnToSeqCommonDimension: Column[Seq[_root_.io.flow.common.v0.models.Dimension]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Dimension]] }
    implicit val columnToMapCommonDimension: Column[Map[String, _root_.io.flow.common.v0.models.Dimension]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Dimension]] }
    implicit val columnToSeqCommonError: Column[Seq[_root_.io.flow.common.v0.models.Error]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Error]] }
    implicit val columnToMapCommonError: Column[Map[String, _root_.io.flow.common.v0.models.Error]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Error]] }
    implicit val columnToSeqCommonHealthcheck: Column[Seq[_root_.io.flow.common.v0.models.Healthcheck]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Healthcheck]] }
    implicit val columnToMapCommonHealthcheck: Column[Map[String, _root_.io.flow.common.v0.models.Healthcheck]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Healthcheck]] }
    implicit val columnToSeqCommonLocale: Column[Seq[_root_.io.flow.common.v0.models.Locale]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Locale]] }
    implicit val columnToMapCommonLocale: Column[Map[String, _root_.io.flow.common.v0.models.Locale]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Locale]] }
    implicit val columnToSeqCommonLocation: Column[Seq[_root_.io.flow.common.v0.models.Location]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Location]] }
    implicit val columnToMapCommonLocation: Column[Map[String, _root_.io.flow.common.v0.models.Location]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Location]] }
    implicit val columnToSeqCommonName: Column[Seq[_root_.io.flow.common.v0.models.Name]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Name]] }
    implicit val columnToMapCommonName: Column[Map[String, _root_.io.flow.common.v0.models.Name]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Name]] }
    implicit val columnToSeqCommonOrganization: Column[Seq[_root_.io.flow.common.v0.models.Organization]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Organization]] }
    implicit val columnToMapCommonOrganization: Column[Map[String, _root_.io.flow.common.v0.models.Organization]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Organization]] }
    implicit val columnToSeqCommonOrganizationReference: Column[Seq[_root_.io.flow.common.v0.models.OrganizationReference]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.OrganizationReference]] }
    implicit val columnToMapCommonOrganizationReference: Column[Map[String, _root_.io.flow.common.v0.models.OrganizationReference]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.OrganizationReference]] }
    implicit val columnToSeqCommonOrganizationSummary: Column[Seq[_root_.io.flow.common.v0.models.OrganizationSummary]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.OrganizationSummary]] }
    implicit val columnToMapCommonOrganizationSummary: Column[Map[String, _root_.io.flow.common.v0.models.OrganizationSummary]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.OrganizationSummary]] }
    implicit val columnToSeqCommonPrice: Column[Seq[_root_.io.flow.common.v0.models.Price]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.Price]] }
    implicit val columnToMapCommonPrice: Column[Map[String, _root_.io.flow.common.v0.models.Price]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.Price]] }
    implicit val columnToSeqCommonUser: Column[Seq[_root_.io.flow.common.v0.models.User]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.User]] }
    implicit val columnToMapCommonUser: Column[Map[String, _root_.io.flow.common.v0.models.User]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.User]] }
    implicit val columnToSeqCommonUserReference: Column[Seq[_root_.io.flow.common.v0.models.UserReference]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.UserReference]] }
    implicit val columnToMapCommonUserReference: Column[Map[String, _root_.io.flow.common.v0.models.UserReference]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.UserReference]] }
    implicit val columnToSeqCommonUserSummary: Column[Seq[_root_.io.flow.common.v0.models.UserSummary]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.UserSummary]] }
    implicit val columnToMapCommonUserSummary: Column[Map[String, _root_.io.flow.common.v0.models.UserSummary]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.UserSummary]] }
    implicit val columnToSeqCommonExpandableOrganization: Column[Seq[_root_.io.flow.common.v0.models.ExpandableOrganization]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ExpandableOrganization]] }
    implicit val columnToMapCommonExpandableOrganization: Column[Map[String, _root_.io.flow.common.v0.models.ExpandableOrganization]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ExpandableOrganization]] }
    implicit val columnToSeqCommonExpandableUser: Column[Seq[_root_.io.flow.common.v0.models.ExpandableUser]] = parser { _.as[Seq[_root_.io.flow.common.v0.models.ExpandableUser]] }
    implicit val columnToMapCommonExpandableUser: Column[Map[String, _root_.io.flow.common.v0.models.ExpandableUser]] = parser { _.as[Map[String, _root_.io.flow.common.v0.models.ExpandableUser]] }

  }

}