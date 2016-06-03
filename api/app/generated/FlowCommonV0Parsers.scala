/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.12
 * apidoc:0.11.27 http://www.apidoc.me/flow/common/0.0.12/anorm_2_x_parsers
 */
import anorm._

package io.flow.common.v0.anorm.parsers {

  import io.flow.common.v0.anorm.conversions.Standard._

  import io.flow.common.v0.anorm.conversions.Types._

  object Calendar {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "calendar"): RowParser[io.flow.common.v0.models.Calendar] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.Calendar(value)
      }
    }

  }

  object Capability {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "capability"): RowParser[io.flow.common.v0.models.Capability] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.Capability(value)
      }
    }

  }

  object ChangeType {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "change_type"): RowParser[io.flow.common.v0.models.ChangeType] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.ChangeType(value)
      }
    }

  }

  object DimensionType {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "dimension_type"): RowParser[io.flow.common.v0.models.DimensionType] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.DimensionType(value)
      }
    }

  }

  object ExceptionType {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "exception_type"): RowParser[io.flow.common.v0.models.ExceptionType] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.ExceptionType(value)
      }
    }

  }

  object HolidayCalendar {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "holiday_calendar"): RowParser[io.flow.common.v0.models.HolidayCalendar] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.HolidayCalendar(value)
      }
    }

  }

  object ScheduleExceptionStatus {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "schedule_exception_status"): RowParser[io.flow.common.v0.models.ScheduleExceptionStatus] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.ScheduleExceptionStatus(value)
      }
    }

  }

  object SortDirection {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "sort_direction"): RowParser[io.flow.common.v0.models.SortDirection] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.SortDirection(value)
      }
    }

  }

  object UnitOfMeasurement {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "unit_of_measurement"): RowParser[io.flow.common.v0.models.UnitOfMeasurement] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.UnitOfMeasurement(value)
      }
    }

  }

  object UnitOfTime {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "unit_of_time"): RowParser[io.flow.common.v0.models.UnitOfTime] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.UnitOfTime(value)
      }
    }

  }

  object ValueAddedService {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "value_added_service"): RowParser[io.flow.common.v0.models.ValueAddedService] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.ValueAddedService(value)
      }
    }

  }

  object Visibility {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "visibility"): RowParser[io.flow.common.v0.models.Visibility] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.Visibility(value)
      }
    }

  }

  object Contact {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      namePrefix = s"$prefix${sep}name",
      email = s"$prefix${sep}email",
      phone = s"$prefix${sep}phone"
    )

    def parser(
      namePrefix: String = "name",
      email: String = "email",
      phone: String = "phone"
    ): RowParser[io.flow.common.v0.models.Contact] = {
      io.flow.common.v0.anorm.parsers.Name.parserWithPrefix(namePrefix) ~
      SqlParser.str(email).? ~
      SqlParser.str(phone).? map {
        case name ~ email ~ phone => {
          io.flow.common.v0.models.Contact(
            name = name,
            email = email,
            phone = phone
          )
        }
      }
    }

  }

  object DatetimeRange {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      from = s"$prefix${sep}from",
      to = s"$prefix${sep}to"
    )

    def parser(
      from: String = "from",
      to: String = "to"
    ): RowParser[io.flow.common.v0.models.DatetimeRange] = {
      SqlParser.get[_root_.org.joda.time.DateTime](from) ~
      SqlParser.get[_root_.org.joda.time.DateTime](to) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      `type` = s"$prefix${sep}type",
      depthPrefix = s"$prefix${sep}depth",
      lengthPrefix = s"$prefix${sep}length",
      weightPrefix = s"$prefix${sep}weight",
      widthPrefix = s"$prefix${sep}width"
    )

    def parser(
      `type`: String = "type",
      depthPrefix: String = "depth",
      lengthPrefix: String = "length",
      weightPrefix: String = "weight",
      widthPrefix: String = "width"
    ): RowParser[io.flow.common.v0.models.Dimension] = {
      io.flow.common.v0.anorm.parsers.DimensionType.parser(`type`) ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(depthPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(lengthPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(weightPrefix).? ~
      io.flow.common.v0.anorm.parsers.Measurement.parserWithPrefix(widthPrefix).? map {
        case typeInstance ~ depth ~ length ~ weight ~ width => {
          io.flow.common.v0.models.Dimension(
            `type` = typeInstance,
            depth = depth,
            length = length,
            weight = weight,
            width = width
          )
        }
      }
    }

  }

  object Error {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      code = s"$prefix${sep}code",
      message = s"$prefix${sep}message"
    )

    def parser(
      code: String = "code",
      message: String = "message"
    ): RowParser[io.flow.common.v0.models.Error] = {
      SqlParser.str(code) ~
      SqlParser.str(message) map {
        case code ~ message => {
          io.flow.common.v0.models.Error(
            code = code,
            message = message
          )
        }
      }
    }

  }

  object Exception {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      `type` = s"$prefix${sep}type",
      datetimeRangePrefix = s"$prefix${sep}datetime_range"
    )

    def parser(
      `type`: String = "type",
      datetimeRangePrefix: String = "datetime_range"
    ): RowParser[io.flow.common.v0.models.Exception] = {
      io.flow.common.v0.anorm.parsers.ExceptionType.parser(`type`) ~
      io.flow.common.v0.anorm.parsers.DatetimeRange.parserWithPrefix(datetimeRangePrefix) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      key = s"$prefix${sep}key",
      name = s"$prefix${sep}name",
      currency = s"$prefix${sep}currency",
      country = s"$prefix${sep}country"
    )

    def parser(
      id: String = "id",
      key: String = "key",
      name: String = "name",
      currency: String = "currency",
      country: String = "country"
    ): RowParser[io.flow.common.v0.models.ExperienceSummary] = {
      SqlParser.str(id) ~
      SqlParser.str(key) ~
      SqlParser.str(name) ~
      SqlParser.str(currency).? ~
      SqlParser.str(country).? map {
        case id ~ key ~ name ~ currency ~ country => {
          io.flow.common.v0.models.ExperienceSummary(
            id = id,
            key = key,
            name = name,
            currency = currency,
            country = country
          )
        }
      }
    }

  }

  object Healthcheck {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      status = s"$prefix${sep}status"
    )

    def parser(
      status: String = "status"
    ): RowParser[io.flow.common.v0.models.Healthcheck] = {
      SqlParser.str(status) map {
        case status => {
          io.flow.common.v0.models.Healthcheck(
            status = status
          )
        }
      }
    }

  }

  object Location {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      text = s"$prefix${sep}text",
      streets = s"$prefix${sep}streets",
      city = s"$prefix${sep}city",
      province = s"$prefix${sep}province",
      postal = s"$prefix${sep}postal",
      country = s"$prefix${sep}country",
      latitude = s"$prefix${sep}latitude",
      longitude = s"$prefix${sep}longitude"
    )

    def parser(
      text: String = "text",
      streets: String = "streets",
      city: String = "city",
      province: String = "province",
      postal: String = "postal",
      country: String = "country",
      latitude: String = "latitude",
      longitude: String = "longitude"
    ): RowParser[io.flow.common.v0.models.Location] = {
      SqlParser.str(text).? ~
      SqlParser.get[Seq[String]](streets).? ~
      SqlParser.str(city).? ~
      SqlParser.str(province).? ~
      SqlParser.str(postal).? ~
      SqlParser.str(country).? ~
      SqlParser.str(latitude).? ~
      SqlParser.str(longitude).? map {
        case text ~ streets ~ city ~ province ~ postal ~ country ~ latitude ~ longitude => {
          io.flow.common.v0.models.Location(
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

  object LocationReference {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      text = s"$prefix${sep}text"
    )

    def parser(
      text: String = "text"
    ): RowParser[io.flow.common.v0.models.LocationReference] = {
      SqlParser.str(text).? map {
        case text => {
          io.flow.common.v0.models.LocationReference(
            text = text
          )
        }
      }
    }

  }

  object Measurement {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      value = s"$prefix${sep}value",
      units = s"$prefix${sep}units"
    )

    def parser(
      value: String = "value",
      units: String = "units"
    ): RowParser[io.flow.common.v0.models.Measurement] = {
      SqlParser.str(value) ~
      io.flow.common.v0.anorm.parsers.UnitOfMeasurement.parser(units) map {
        case value ~ units => {
          io.flow.common.v0.models.Measurement(
            value = value,
            units = units
          )
        }
      }
    }

  }

  object Name {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      first = s"$prefix${sep}first",
      last = s"$prefix${sep}last"
    )

    def parser(
      first: String = "first",
      last: String = "last"
    ): RowParser[io.flow.common.v0.models.Name] = {
      SqlParser.str(first).? ~
      SqlParser.str(last).? map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      name = s"$prefix${sep}name"
    )

    def parser(
      id: String = "id",
      name: String = "name"
    ): RowParser[io.flow.common.v0.models.Organization] = {
      SqlParser.str(id) ~
      SqlParser.str(name) map {
        case id ~ name => {
          io.flow.common.v0.models.Organization(
            id = id,
            name = name
          )
        }
      }
    }

  }

  object OrganizationReference {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id"
    )

    def parser(
      id: String = "id"
    ): RowParser[io.flow.common.v0.models.OrganizationReference] = {
      SqlParser.str(id) map {
        case id => {
          io.flow.common.v0.models.OrganizationReference(
            id = id
          )
        }
      }
    }

  }

  object OrganizationSummary {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      name = s"$prefix${sep}name"
    )

    def parser(
      id: String = "id",
      name: String = "name"
    ): RowParser[io.flow.common.v0.models.OrganizationSummary] = {
      SqlParser.str(id) ~
      SqlParser.str(name) map {
        case id ~ name => {
          io.flow.common.v0.models.OrganizationSummary(
            id = id,
            name = name
          )
        }
      }
    }

  }

  object Schedule {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      calendar = s"$prefix${sep}calendar",
      holiday = s"$prefix${sep}holiday",
      exception = s"$prefix${sep}exception",
      cutoff = s"$prefix${sep}cutoff"
    )

    def parser(
      calendar: String = "calendar",
      holiday: String = "holiday",
      exception: String = "exception",
      cutoff: String = "cutoff"
    ): RowParser[io.flow.common.v0.models.Schedule] = {
      io.flow.common.v0.anorm.parsers.Calendar.parser(calendar).? ~
      io.flow.common.v0.anorm.parsers.HolidayCalendar.parser(holiday) ~
      SqlParser.get[Seq[io.flow.common.v0.models.Exception]](exception) ~
      SqlParser.str(cutoff).? map {
        case calendar ~ holiday ~ exception ~ cutoff => {
          io.flow.common.v0.models.Schedule(
            calendar = calendar,
            holiday = holiday,
            exception = exception,
            cutoff = cutoff
          )
        }
      }
    }

  }

  object User {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      email = s"$prefix${sep}email",
      namePrefix = s"$prefix${sep}name"
    )

    def parser(
      id: String = "id",
      email: String = "email",
      namePrefix: String = "name"
    ): RowParser[io.flow.common.v0.models.User] = {
      SqlParser.str(id) ~
      SqlParser.str(email).? ~
      io.flow.common.v0.anorm.parsers.Name.parserWithPrefix(namePrefix) map {
        case id ~ email ~ name => {
          io.flow.common.v0.models.User(
            id = id,
            email = email,
            name = name
          )
        }
      }
    }

  }

  object UserReference {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id"
    )

    def parser(
      id: String = "id"
    ): RowParser[io.flow.common.v0.models.UserReference] = {
      SqlParser.str(id) map {
        case id => {
          io.flow.common.v0.models.UserReference(
            id = id
          )
        }
      }
    }

  }

  object UserSummary {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      email = s"$prefix${sep}email",
      name = s"$prefix${sep}name"
    )

    def parser(
      id: String = "id",
      email: String = "email",
      name: String = "name"
    ): RowParser[io.flow.common.v0.models.UserSummary] = {
      SqlParser.str(id) ~
      SqlParser.str(email).? ~
      SqlParser.str(name) map {
        case id ~ email ~ name => {
          io.flow.common.v0.models.UserSummary(
            id = id,
            email = email,
            name = name
          )
        }
      }
    }

  }

  object ExpandableLocation {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.common.v0.anorm.parsers.Location.parserWithPrefix(prefix, sep) |
      io.flow.common.v0.anorm.parsers.LocationReference.parserWithPrefix(prefix, sep)
    }

    def parser() = {
      io.flow.common.v0.anorm.parsers.Location.parser() |
      io.flow.common.v0.anorm.parsers.LocationReference.parser()
    }

  }

  object ExpandableOrganization {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.common.v0.anorm.parsers.Organization.parserWithPrefix(prefix, sep) |
      io.flow.common.v0.anorm.parsers.OrganizationReference.parserWithPrefix(prefix, sep)
    }

    def parser() = {
      io.flow.common.v0.anorm.parsers.Organization.parser() |
      io.flow.common.v0.anorm.parsers.OrganizationReference.parser()
    }

  }

  object ExpandableUser {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.common.v0.anorm.parsers.User.parserWithPrefix(prefix, sep) |
      io.flow.common.v0.anorm.parsers.UserReference.parserWithPrefix(prefix, sep)
    }

    def parser() = {
      io.flow.common.v0.anorm.parsers.User.parser() |
      io.flow.common.v0.anorm.parsers.UserReference.parser()
    }

  }

}