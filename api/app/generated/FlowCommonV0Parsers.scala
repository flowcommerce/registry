/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.4.50
 * apibuilder:0.13.0 https://app.apibuilder.io/flow/common/0.4.50/anorm_2_x_parsers
 */
import anorm._

package io.flow.common.v0.anorm.parsers {

  import io.flow.common.v0.anorm.conversions.Standard._

  import io.flow.common.v0.anorm.conversions.Types._

  object AvailabilityStatus {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.AvailabilityStatus] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "availability_status", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.AvailabilityStatus] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.AvailabilityStatus(value)
      }
    }

  }

  object Calendar {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Calendar] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "calendar", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.Calendar] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.Calendar(value)
      }
    }

  }

  object Capability {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Capability] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "capability", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.Capability] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.Capability(value)
      }
    }

  }

  object ChangeType {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.ChangeType] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "change_type", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.ChangeType] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.ChangeType(value)
      }
    }

  }

  object CurrencyLabelFormatter {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.CurrencyLabelFormatter] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "currency_label_formatter", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.CurrencyLabelFormatter] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.CurrencyLabelFormatter(value)
      }
    }

  }

  object CurrencySymbolFormat {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.CurrencySymbolFormat] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "currency_symbol_format", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.CurrencySymbolFormat] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.CurrencySymbolFormat(value)
      }
    }

  }

  object DayOfWeek {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.DayOfWeek] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "day_of_week", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.DayOfWeek] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.DayOfWeek(value)
      }
    }

  }

  object DeliveredDuty {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.DeliveredDuty] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "delivered_duty", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.DeliveredDuty] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.DeliveredDuty(value)
      }
    }

  }

  object Environment {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Environment] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "environment", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.Environment] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.Environment(value)
      }
    }

  }

  object ExceptionType {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.ExceptionType] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "exception_type", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.ExceptionType] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.ExceptionType(value)
      }
    }

  }

  object HolidayCalendar {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.HolidayCalendar] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "holiday_calendar", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.HolidayCalendar] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.HolidayCalendar(value)
      }
    }

  }

  object IncludedLevyKey {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.IncludedLevyKey] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "included_levy_key", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.IncludedLevyKey] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.IncludedLevyKey(value)
      }
    }

  }

  object MarginType {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.MarginType] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "margin_type", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.MarginType] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.MarginType(value)
      }
    }

  }

  object MeasurementSystem {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.MeasurementSystem] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "measurement_system", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.MeasurementSystem] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.MeasurementSystem(value)
      }
    }

  }

  object PriceBookStatus {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.PriceBookStatus] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "price_book_status", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.PriceBookStatus] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.PriceBookStatus(value)
      }
    }

  }

  object Role {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Role] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "role", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.Role] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.Role(value)
      }
    }

  }

  object RoundingMethod {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.RoundingMethod] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "rounding_method", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.RoundingMethod] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.RoundingMethod(value)
      }
    }

  }

  object RoundingType {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.RoundingType] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "rounding_type", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.RoundingType] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.RoundingType(value)
      }
    }

  }

  object ScheduleExceptionStatus {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.ScheduleExceptionStatus] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "schedule_exception_status", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.ScheduleExceptionStatus] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.ScheduleExceptionStatus(value)
      }
    }

  }

  object SortDirection {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.SortDirection] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "sort_direction", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.SortDirection] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.SortDirection(value)
      }
    }

  }

  object UnitOfMeasurement {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.UnitOfMeasurement] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "unit_of_measurement", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.UnitOfMeasurement] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.UnitOfMeasurement(value)
      }
    }

  }

  object UnitOfTime {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.UnitOfTime] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "unit_of_time", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.UnitOfTime] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.UnitOfTime(value)
      }
    }

  }

  object UserStatus {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.UserStatus] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "user_status", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.UserStatus] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.UserStatus(value)
      }
    }

  }

  object ValueAddedService {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.ValueAddedService] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "value_added_service", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.ValueAddedService] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.ValueAddedService(value)
      }
    }

  }

  object Visibility {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Visibility] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(name: String = "visibility", prefixOpt: Option[String] = None): RowParser[io.flow.common.v0.models.Visibility] = {
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case value => io.flow.common.v0.models.Visibility(value)
      }
    }

  }

  object Address {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Address] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      text: String = "text",
      streets: String = "streets",
      city: String = "city",
      province: String = "province",
      postal: String = "postal",
      country: String = "country",
      latitude: String = "latitude",
      longitude: String = "longitude",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Address] = {
      SqlParser.str(prefixOpt.getOrElse("") + text).? ~
      SqlParser.get[Seq[String]](prefixOpt.getOrElse("") + streets).? ~
      SqlParser.str(prefixOpt.getOrElse("") + city).? ~
      SqlParser.str(prefixOpt.getOrElse("") + province).? ~
      SqlParser.str(prefixOpt.getOrElse("") + postal).? ~
      SqlParser.str(prefixOpt.getOrElse("") + country).? ~
      SqlParser.str(prefixOpt.getOrElse("") + latitude).? ~
      SqlParser.str(prefixOpt.getOrElse("") + longitude).? map {
        case text ~ streets ~ city ~ province ~ postal ~ country ~ latitude ~ longitude => {
          io.flow.common.v0.models.Address(
            text = text,
            streets = streets,
            city = city,
            province = province,
            postal = postal,
            country = country,
            latitude = latitude,
            longitude = longitude
          )
        }
      }
    }

  }

  object CatalogItemReference {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.CatalogItemReference] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      number: String = "number",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.CatalogItemReference] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + number) map {
        case id ~ number => {
          io.flow.common.v0.models.CatalogItemReference(
            id = id,
            number = number
          )
        }
      }
    }

  }

  object CatalogItemSummary {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.CatalogItemSummary] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      number: String = "number",
      name: String = "name",
      attributes: String = "attributes",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.CatalogItemSummary] = {
      SqlParser.str(prefixOpt.getOrElse("") + number) ~
      SqlParser.str(prefixOpt.getOrElse("") + name) ~
      SqlParser.get[Map[String, String]](prefixOpt.getOrElse("") + attributes) map {
        case number ~ name ~ attributes => {
          io.flow.common.v0.models.CatalogItemSummary(
            number = number,
            name = name,
            attributes = attributes
          )
        }
      }
    }

  }

  object Contact {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Contact] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      namePrefix: String = "name",
      company: String = "company",
      email: String = "email",
      phone: String = "phone",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Contact] = {
      io.flow.common.v0.anorm.parsers.Name.parserWithPrefix(prefixOpt.getOrElse("") + namePrefix) ~
      SqlParser.str(prefixOpt.getOrElse("") + company).? ~
      SqlParser.str(prefixOpt.getOrElse("") + email).? ~
      SqlParser.str(prefixOpt.getOrElse("") + phone).? map {
        case name ~ company ~ email ~ phone => {
          io.flow.common.v0.models.Contact(
            name = name,
            company = company,
            email = email,
            phone = phone
          )
        }
      }
    }

  }

  object Customer {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Customer] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      namePrefix: String = "name",
      number: String = "number",
      phone: String = "phone",
      email: String = "email",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Customer] = {
      io.flow.common.v0.anorm.parsers.Name.parserWithPrefix(prefixOpt.getOrElse("") + namePrefix) ~
      SqlParser.str(prefixOpt.getOrElse("") + number).? ~
      SqlParser.str(prefixOpt.getOrElse("") + phone).? ~
      SqlParser.str(prefixOpt.getOrElse("") + email).? map {
        case name ~ number ~ phone ~ email => {
          io.flow.common.v0.models.Customer(
            name = name,
            number = number,
            phone = phone,
            email = email
          )
        }
      }
    }

  }

  object DatetimeRange {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.DatetimeRange] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      from: String = "from",
      to: String = "to",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.DatetimeRange] = {
      SqlParser.get[_root_.org.joda.time.DateTime](prefixOpt.getOrElse("") + from) ~
      SqlParser.get[_root_.org.joda.time.DateTime](prefixOpt.getOrElse("") + to) map {
        case from ~ to => {
          io.flow.common.v0.models.DatetimeRange(
            from = from,
            to = to
          )
        }
      }
    }

  }

  object Dimension {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Dimension] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      depthPrefix: String = "depth",
      diameterPrefix: String = "diameter",
      lengthPrefix: String = "length",
      weightPrefix: String = "weight",
      widthPrefix: String = "width",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Dimension] = {
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(prefixOpt.getOrElse("") + depthPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(prefixOpt.getOrElse("") + diameterPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(prefixOpt.getOrElse("") + lengthPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(prefixOpt.getOrElse("") + weightPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(prefixOpt.getOrElse("") + widthPrefix).? map {
        case depth ~ diameter ~ length ~ weight ~ width => {
          io.flow.common.v0.models.Dimension(
            depth = depth,
            diameter = diameter,
            length = length,
            weight = weight,
            width = width
          )
        }
      }
    }

  }

  object Dimensions {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Dimensions] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      productPrefix: String = "product",
      packagingPrefix: String = "packaging",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Dimensions] = {
      io.flow.common.v0.anorm.parsers.Dimension.parserWithPrefix(prefixOpt.getOrElse("") + productPrefix).? ~
      io.flow.common.v0.anorm.parsers.Dimension.parserWithPrefix(prefixOpt.getOrElse("") + packagingPrefix).? map {
        case product ~ packaging => {
          io.flow.common.v0.models.Dimensions(
            product = product,
            packaging = packaging
          )
        }
      }
    }

  }

  object Exception {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Exception] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      `type`: String = "type",
      datetimeRangePrefix: String = "datetime_range",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Exception] = {
      io.flow.common.v0.anorm.parsers.ExceptionType.parser(prefixOpt.getOrElse("") + `type`) ~
      io.flow.common.v0.anorm.parsers.DatetimeRange.parserWithPrefix(prefixOpt.getOrElse("") + datetimeRangePrefix) map {
        case typeInstance ~ datetimeRange => {
          io.flow.common.v0.models.Exception(
            `type` = typeInstance,
            datetimeRange = datetimeRange
          )
        }
      }
    }

  }

  object ExperienceSummary {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.ExperienceSummary] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      key: String = "key",
      name: String = "name",
      country: String = "country",
      currency: String = "currency",
      language: String = "language",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.ExperienceSummary] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + key) ~
      SqlParser.str(prefixOpt.getOrElse("") + name) ~
      SqlParser.str(prefixOpt.getOrElse("") + country).? ~
      SqlParser.str(prefixOpt.getOrElse("") + currency).? ~
      SqlParser.str(prefixOpt.getOrElse("") + language).? map {
        case id ~ key ~ name ~ country ~ currency ~ language => {
          io.flow.common.v0.models.ExperienceSummary(
            id = id,
            key = key,
            name = name,
            country = country,
            currency = currency,
            language = language
          )
        }
      }
    }

  }

  object IncludedLevies {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.IncludedLevies] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      key: String = "key",
      label: String = "label",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.IncludedLevies] = {
      io.flow.common.v0.anorm.parsers.IncludedLevyKey.parser(prefixOpt.getOrElse("") + key) ~
      SqlParser.str(prefixOpt.getOrElse("") + label) map {
        case key ~ label => {
          io.flow.common.v0.models.IncludedLevies(
            key = key,
            label = label
          )
        }
      }
    }

  }

  object LineItem {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.LineItem] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      number: String = "number",
      quantity: String = "quantity",
      pricePrefix: String = "price",
      attributes: String = "attributes",
      center: String = "center",
      discountPrefix: String = "discount",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.LineItem] = {
      SqlParser.str(prefixOpt.getOrElse("") + number) ~
      SqlParser.long(prefixOpt.getOrElse("") + quantity) ~
      io.flow.common.v0.anorm.parsers.Money.parserWithPrefix(prefixOpt.getOrElse("") + pricePrefix) ~
      SqlParser.get[Map[String, String]](prefixOpt.getOrElse("") + attributes) ~
      SqlParser.str(prefixOpt.getOrElse("") + center).? ~
      io.flow.common.v0.anorm.parsers.Money.parserWithPrefix(prefixOpt.getOrElse("") + discountPrefix).? map {
        case number ~ quantity ~ price ~ attributes ~ center ~ discount => {
          io.flow.common.v0.models.LineItem(
            number = number,
            quantity = quantity,
            price = price,
            attributes = attributes,
            center = center,
            discount = discount
          )
        }
      }
    }

  }

  object LineItemForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.LineItemForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      number: String = "number",
      quantity: String = "quantity",
      shipmentEstimatePrefix: String = "shipment_estimate",
      pricePrefix: String = "price",
      attributes: String = "attributes",
      center: String = "center",
      discountPrefix: String = "discount",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.LineItemForm] = {
      SqlParser.str(prefixOpt.getOrElse("") + number) ~
      SqlParser.long(prefixOpt.getOrElse("") + quantity) ~
      io.flow.common.v0.anorm.parsers.DatetimeRange.parserWithPrefix(prefixOpt.getOrElse("") + shipmentEstimatePrefix).? ~
      io.flow.common.v0.anorm.parsers.Money.parserWithPrefix(prefixOpt.getOrElse("") + pricePrefix).? ~
      SqlParser.get[Map[String, String]](prefixOpt.getOrElse("") + attributes).? ~
      SqlParser.str(prefixOpt.getOrElse("") + center).? ~
      io.flow.common.v0.anorm.parsers.Money.parserWithPrefix(prefixOpt.getOrElse("") + discountPrefix).? map {
        case number ~ quantity ~ shipmentEstimate ~ price ~ attributes ~ center ~ discount => {
          io.flow.common.v0.models.LineItemForm(
            number = number,
            quantity = quantity,
            shipmentEstimate = shipmentEstimate,
            price = price,
            attributes = attributes,
            center = center,
            discount = discount
          )
        }
      }
    }

  }

  object Margin {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Margin] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      `type`: String = "type",
      value: String = "value",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Margin] = {
      io.flow.common.v0.anorm.parsers.MarginType.parser(prefixOpt.getOrElse("") + `type`) ~
      SqlParser.get[BigDecimal](prefixOpt.getOrElse("") + value) map {
        case typeInstance ~ value => {
          io.flow.common.v0.models.Margin(
            `type` = typeInstance,
            value = value
          )
        }
      }
    }

  }

  object Measurement {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Measurement] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      value: String = "value",
      units: String = "units",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Measurement] = {
      SqlParser.str(prefixOpt.getOrElse("") + value) ~
      io.flow.common.v0.anorm.parsers.UnitOfMeasurement.parser(prefixOpt.getOrElse("") + units) map {
        case value ~ units => {
          io.flow.common.v0.models.Measurement(
            value = value,
            units = units
          )
        }
      }
    }

  }

  object Money {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Money] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      amount: String = "amount",
      currency: String = "currency",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Money] = {
      SqlParser.get[Double](prefixOpt.getOrElse("") + amount) ~
      SqlParser.str(prefixOpt.getOrElse("") + currency) map {
        case amount ~ currency => {
          io.flow.common.v0.models.Money(
            amount = amount,
            currency = currency
          )
        }
      }
    }

  }

  object Name {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Name] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      first: String = "first",
      last: String = "last",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Name] = {
      SqlParser.str(prefixOpt.getOrElse("") + first).? ~
      SqlParser.str(prefixOpt.getOrElse("") + last).? map {
        case first ~ last => {
          io.flow.common.v0.models.Name(
            first = first,
            last = last
          )
        }
      }
    }

  }

  object Organization {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Organization] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      name: String = "name",
      environment: String = "environment",
      parentPrefix: String = "parent",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Organization] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + name) ~
      io.flow.common.v0.anorm.parsers.Environment.parser(prefixOpt.getOrElse("") + environment) ~
      io.flow.common.v0.anorm.parsers.OrganizationReference.parserWithPrefix(prefixOpt.getOrElse("") + parentPrefix).? map {
        case id ~ name ~ environment ~ parent => {
          io.flow.common.v0.models.Organization(
            id = id,
            name = name,
            environment = environment,
            parent = parent
          )
        }
      }
    }

  }

  object OrganizationReference {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.OrganizationReference] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.OrganizationReference] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) map {
        case id => {
          io.flow.common.v0.models.OrganizationReference(
            id = id
          )
        }
      }
    }

  }

  object OrganizationSummary {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.OrganizationSummary] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      name: String = "name",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.OrganizationSummary] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + name) map {
        case id ~ name => {
          io.flow.common.v0.models.OrganizationSummary(
            id = id,
            name = name
          )
        }
      }
    }

  }

  object Price {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Price] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      amount: String = "amount",
      currency: String = "currency",
      label: String = "label",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Price] = {
      SqlParser.get[Double](prefixOpt.getOrElse("") + amount) ~
      SqlParser.str(prefixOpt.getOrElse("") + currency) ~
      SqlParser.str(prefixOpt.getOrElse("") + label) map {
        case amount ~ currency ~ label => {
          io.flow.common.v0.models.Price(
            amount = amount,
            currency = currency,
            label = label
          )
        }
      }
    }

  }

  object PriceForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.PriceForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      amount: String = "amount",
      currency: String = "currency",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.PriceForm] = {
      SqlParser.get[Double](prefixOpt.getOrElse("") + amount) ~
      SqlParser.str(prefixOpt.getOrElse("") + currency) map {
        case amount ~ currency => {
          io.flow.common.v0.models.PriceForm(
            amount = amount,
            currency = currency
          )
        }
      }
    }

  }

  object PriceWithBase {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.PriceWithBase] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      currency: String = "currency",
      amount: String = "amount",
      label: String = "label",
      basePrefix: String = "base",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.PriceWithBase] = {
      SqlParser.str(prefixOpt.getOrElse("") + currency) ~
      SqlParser.get[Double](prefixOpt.getOrElse("") + amount) ~
      SqlParser.str(prefixOpt.getOrElse("") + label) ~
      io.flow.common.v0.anorm.parsers.Price.parserWithPrefix(prefixOpt.getOrElse("") + basePrefix).? map {
        case currency ~ amount ~ label ~ base => {
          io.flow.common.v0.models.PriceWithBase(
            currency = currency,
            amount = amount,
            label = label,
            base = base
          )
        }
      }
    }

  }

  object Rounding {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Rounding] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      `type`: String = "type",
      method: String = "method",
      value: String = "value",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Rounding] = {
      io.flow.common.v0.anorm.parsers.RoundingType.parser(prefixOpt.getOrElse("") + `type`) ~
      io.flow.common.v0.anorm.parsers.RoundingMethod.parser(prefixOpt.getOrElse("") + method) ~
      SqlParser.get[BigDecimal](prefixOpt.getOrElse("") + value) map {
        case typeInstance ~ method ~ value => {
          io.flow.common.v0.models.Rounding(
            `type` = typeInstance,
            method = method,
            value = value
          )
        }
      }
    }

  }

  object Schedule {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Schedule] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      calendar: String = "calendar",
      holiday: String = "holiday",
      exception: String = "exception",
      cutoff: String = "cutoff",
      minLeadTime: String = "min_lead_time",
      maxLeadTime: String = "max_lead_time",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Schedule] = {
      io.flow.common.v0.anorm.parsers.Calendar.parser(prefixOpt.getOrElse("") + calendar).? ~
      io.flow.common.v0.anorm.parsers.HolidayCalendar.parser(prefixOpt.getOrElse("") + holiday) ~
      SqlParser.get[Seq[io.flow.common.v0.models.Exception]](prefixOpt.getOrElse("") + exception) ~
      SqlParser.str(prefixOpt.getOrElse("") + cutoff).? ~
      SqlParser.long(prefixOpt.getOrElse("") + minLeadTime).? ~
      SqlParser.long(prefixOpt.getOrElse("") + maxLeadTime).? map {
        case calendar ~ holiday ~ exception ~ cutoff ~ minLeadTime ~ maxLeadTime => {
          io.flow.common.v0.models.Schedule(
            calendar = calendar,
            holiday = holiday,
            exception = exception,
            cutoff = cutoff,
            minLeadTime = minLeadTime,
            maxLeadTime = maxLeadTime
          )
        }
      }
    }

  }

  object User {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.User] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      email: String = "email",
      namePrefix: String = "name",
      status: String = "status",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.User] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + email).? ~
      io.flow.common.v0.anorm.parsers.Name.parserWithPrefix(prefixOpt.getOrElse("") + namePrefix) ~
      io.flow.common.v0.anorm.parsers.UserStatus.parser(prefixOpt.getOrElse("") + status) map {
        case id ~ email ~ name ~ status => {
          io.flow.common.v0.models.User(
            id = id,
            email = email,
            name = name,
            status = status
          )
        }
      }
    }

  }

  object UserReference {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.UserReference] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.UserReference] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) map {
        case id => {
          io.flow.common.v0.models.UserReference(
            id = id
          )
        }
      }
    }

  }

  object Zone {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.common.v0.models.Zone] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      province: String = "province",
      country: String = "country",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.common.v0.models.Zone] = {
      SqlParser.str(prefixOpt.getOrElse("") + province).? ~
      SqlParser.str(prefixOpt.getOrElse("") + country) map {
        case province ~ country => {
          io.flow.common.v0.models.Zone(
            province = province,
            country = country
          )
        }
      }
    }

  }

  object ExpandableOrganization {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.common.v0.anorm.parsers.Organization.parser(prefixOpt = Some(s"$prefix$sep")) |
      io.flow.common.v0.anorm.parsers.OrganizationReference.parser(prefixOpt = Some(s"$prefix$sep"))
    }

    def parser() = {
      io.flow.common.v0.anorm.parsers.Organization.parser() |
      io.flow.common.v0.anorm.parsers.OrganizationReference.parser()
    }

  }

  object ExpandableUser {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.common.v0.anorm.parsers.User.parser(prefixOpt = Some(s"$prefix$sep")) |
      io.flow.common.v0.anorm.parsers.UserReference.parser(prefixOpt = Some(s"$prefix$sep"))
    }

    def parser() = {
      io.flow.common.v0.anorm.parsers.User.parser() |
      io.flow.common.v0.anorm.parsers.UserReference.parser()
    }

  }

}