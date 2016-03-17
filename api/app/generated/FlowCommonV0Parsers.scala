/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.11
 * apidoc:0.11.19 http://www.apidoc.me/flow/common/0.0.11/anorm_2_x_parsers
 */
import anorm._

package io.flow.common.v0.anorm.parsers {

  import io.flow.common.v0.anorm.conversions.Json._

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

  object ScheduleExceptionStatus {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(s"$prefix${sep}name")

    def parser(name: String = "schedule_exception_status"): RowParser[io.flow.common.v0.models.ScheduleExceptionStatus] = {
      SqlParser.str(name) map {
        case value => io.flow.common.v0.models.ScheduleExceptionStatus(value)
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

  object Address {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      text = s"$prefix${sep}text",
      streets = s"$prefix${sep}streets",
      city = s"$prefix${sep}city",
      province = s"$prefix${sep}province",
      postalCode = s"$prefix${sep}postal_code",
      country = s"$prefix${sep}country"
    )

    def parser(
      text: String = "text",
      streets: String = "streets",
      city: String = "city",
      province: String = "province",
      postalCode: String = "postal_code",
      country: String = "country"
    ): RowParser[io.flow.common.v0.models.Address] = {
      SqlParser.str(text).? ~
      SqlParser.get[Seq[String]](streets).? ~
      SqlParser.str(city).? ~
      SqlParser.str(province).? ~
      SqlParser.str(postalCode).? ~
      SqlParser.str(country).? map {
        case text ~ streets ~ city ~ province ~ postalCode ~ country => {
          io.flow.common.v0.models.Address(
            text = text,
            streets = streets,
            city = city,
            province = province,
            postalCode = postalCode,
            country = country
          )
        }
      }
    }

  }

  object ChangeHeader {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      timestamp = s"$prefix${sep}timestamp",
      `type` = s"$prefix${sep}type"
    )

    def parser(
      id: String = "id",
      timestamp: String = "timestamp",
      `type`: String = "type"
    ): RowParser[io.flow.common.v0.models.ChangeHeader] = {
      SqlParser.str(id) ~
      SqlParser.get[_root_.org.joda.time.DateTime](timestamp) ~
      io.flow.common.v0.anorm.parsers.ChangeType.parser(`type`) map {
        case id ~ timestamp ~ typeInstance => {
          io.flow.common.v0.models.ChangeHeader(
            id = id,
            timestamp = timestamp,
            `type` = typeInstance
          )
        }
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
      value = s"$prefix${sep}value",
      units = s"$prefix${sep}units"
    )

    def parser(
      value: String = "value",
      units: String = "units"
    ): RowParser[io.flow.common.v0.models.Dimension] = {
      SqlParser.get[Double](value) ~
      io.flow.common.v0.anorm.parsers.UnitOfMeasurement.parser(units) map {
        case value ~ units => {
          io.flow.common.v0.models.Dimension(
            value = value,
            units = units
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
      value = s"$prefix${sep}value"
    )

    def parser(
      value: String = "value"
    ): RowParser[io.flow.common.v0.models.Location] = {
      SqlParser.str(value) map {
        case value => {
          io.flow.common.v0.models.Location(
            value = value
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

  object Price {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      amount = s"$prefix${sep}amount",
      currency = s"$prefix${sep}currency"
    )

    def parser(
      amount: String = "amount",
      currency: String = "currency"
    ): RowParser[io.flow.common.v0.models.Price] = {
      SqlParser.str(amount) ~
      SqlParser.str(currency) map {
        case amount ~ currency => {
          io.flow.common.v0.models.Price(
            amount = amount,
            currency = currency
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