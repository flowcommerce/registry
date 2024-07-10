/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.3.41
 * User agent: apibuilder app.apibuilder.io/flow/registry/latest/anorm_2_8_parsers
 */
import anorm._

package io.flow.registry.v0.anorm.parsers {

  import io.flow.registry.v0.anorm.conversions.Standard._

  import io.flow.common.v0.anorm.conversions.Types._
  import io.flow.registry.v0.anorm.conversions.Types._

  object Application {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.Application] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      ports: String = "ports",
      dependencies: String = "dependencies",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.Application] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.get[Seq[io.flow.registry.v0.models.Port]](prefixOpt.getOrElse("") + ports) ~
      SqlParser.get[Seq[String]](prefixOpt.getOrElse("") + dependencies) map {
        case id ~ ports ~ dependencies => {
          io.flow.registry.v0.models.Application(
            id = id,
            ports = ports,
            dependencies = dependencies
          )
        }
      }
    }

  }

  object ApplicationForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ApplicationForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      service: String = "service",
      external: String = "external",
      internal: String = "internal",
      dependency: String = "dependency",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ApplicationForm] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.str(prefixOpt.getOrElse("") + service) ~
      SqlParser.long(prefixOpt.getOrElse("") + external).? ~
      SqlParser.long(prefixOpt.getOrElse("") + internal).? ~
      SqlParser.get[Seq[String]](prefixOpt.getOrElse("") + dependency).? map {
        case id ~ service ~ external ~ internal ~ dependency => {
          io.flow.registry.v0.models.ApplicationForm(
            id = id,
            service = service,
            external = external,
            internal = internal,
            dependency = dependency
          )
        }
      }
    }

  }

  object ApplicationPutForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ApplicationPutForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      service: String = "service",
      external: String = "external",
      internal: String = "internal",
      dependency: String = "dependency",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ApplicationPutForm] = {
      SqlParser.str(prefixOpt.getOrElse("") + service).? ~
      SqlParser.long(prefixOpt.getOrElse("") + external).? ~
      SqlParser.long(prefixOpt.getOrElse("") + internal).? ~
      SqlParser.get[Seq[String]](prefixOpt.getOrElse("") + dependency).? map {
        case service ~ external ~ internal ~ dependency => {
          io.flow.registry.v0.models.ApplicationPutForm(
            service = service,
            external = external,
            internal = internal,
            dependency = dependency
          )
        }
      }
    }

  }

  object ApplicationVersion {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ApplicationVersion] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      timestamp: String = "timestamp",
      `type`: String = "type",
      applicationPrefix: String = "application",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ApplicationVersion] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.get[_root_.org.joda.time.DateTime](prefixOpt.getOrElse("") + timestamp) ~
      io.flow.common.v0.anorm.parsers.ChangeType.parser(prefixOpt.getOrElse("") + `type`) ~
      io.flow.registry.v0.anorm.parsers.Application.parserWithPrefix(prefixOpt.getOrElse("") + applicationPrefix) map {
        case id ~ timestamp ~ typeInstance ~ application => {
          io.flow.registry.v0.models.ApplicationVersion(
            id = id,
            timestamp = timestamp,
            `type` = typeInstance,
            application = application
          )
        }
      }
    }

  }

  object Http {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.Http] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      host: String = "host",
      port: String = "port",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.Http] = {
      SqlParser.str(prefixOpt.getOrElse("") + host) ~
      SqlParser.long(prefixOpt.getOrElse("") + port) map {
        case host ~ port => {
          io.flow.registry.v0.models.Http(
            host = host,
            port = port
          )
        }
      }
    }

  }

  object Port {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.Port] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      servicePrefix: String = "service",
      external: String = "external",
      internal: String = "internal",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.Port] = {
      io.flow.registry.v0.anorm.parsers.ServiceReference.parserWithPrefix(prefixOpt.getOrElse("") + servicePrefix) ~
      SqlParser.long(prefixOpt.getOrElse("") + external) ~
      SqlParser.long(prefixOpt.getOrElse("") + internal) map {
        case service ~ external ~ internal => {
          io.flow.registry.v0.models.Port(
            service = service,
            external = external,
            internal = internal
          )
        }
      }
    }

  }

  object Postgresql {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.Postgresql] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      dbname: String = "dbname",
      host: String = "host",
      port: String = "port",
      user: String = "user",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.Postgresql] = {
      SqlParser.str(prefixOpt.getOrElse("") + dbname) ~
      SqlParser.str(prefixOpt.getOrElse("") + host) ~
      SqlParser.long(prefixOpt.getOrElse("") + port) ~
      SqlParser.str(prefixOpt.getOrElse("") + user) map {
        case dbname ~ host ~ port ~ user => {
          io.flow.registry.v0.models.Postgresql(
            dbname = dbname,
            host = host,
            port = port,
            user = user
          )
        }
      }
    }

  }

  object Service {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.Service] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      defaultPort: String = "default_port",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.Service] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.long(prefixOpt.getOrElse("") + defaultPort) map {
        case id ~ defaultPort => {
          io.flow.registry.v0.models.Service(
            id = id,
            defaultPort = defaultPort
          )
        }
      }
    }

  }

  object ServiceForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ServiceForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      defaultPort: String = "default_port",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ServiceForm] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.long(prefixOpt.getOrElse("") + defaultPort) map {
        case id ~ defaultPort => {
          io.flow.registry.v0.models.ServiceForm(
            id = id,
            defaultPort = defaultPort
          )
        }
      }
    }

  }

  object ServicePutForm {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ServicePutForm] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      defaultPort: String = "default_port",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ServicePutForm] = {
      SqlParser.long(prefixOpt.getOrElse("") + defaultPort) map {
        case defaultPort => {
          io.flow.registry.v0.models.ServicePutForm(
            defaultPort = defaultPort
          )
        }
      }
    }

  }

  object ServiceReference {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ServiceReference] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ServiceReference] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) map {
        case id => {
          io.flow.registry.v0.models.ServiceReference(
            id = id
          )
        }
      }
    }

  }

  object ServiceVersion {

    def parserWithPrefix(prefix: String, sep: String = "_"): RowParser[io.flow.registry.v0.models.ServiceVersion] = parser(prefixOpt = Some(s"$prefix$sep"))

    def parser(
      id: String = "id",
      timestamp: String = "timestamp",
      `type`: String = "type",
      servicePrefix: String = "service",
      prefixOpt: Option[String] = None
    ): RowParser[io.flow.registry.v0.models.ServiceVersion] = {
      SqlParser.str(prefixOpt.getOrElse("") + id) ~
      SqlParser.get[_root_.org.joda.time.DateTime](prefixOpt.getOrElse("") + timestamp) ~
      io.flow.common.v0.anorm.parsers.ChangeType.parser(prefixOpt.getOrElse("") + `type`) ~
      io.flow.registry.v0.anorm.parsers.Service.parserWithPrefix(prefixOpt.getOrElse("") + servicePrefix) map {
        case id ~ timestamp ~ typeInstance ~ service => {
          io.flow.registry.v0.models.ServiceVersion(
            id = id,
            timestamp = timestamp,
            `type` = typeInstance,
            service = service
          )
        }
      }
    }

  }

  object Healthcheck {

    def parserWithPrefix(prefix: String, sep: String = "_") = {
      io.flow.registry.v0.anorm.parsers.Http.parser(prefixOpt = Some(s"$prefix$sep")) |
      io.flow.registry.v0.anorm.parsers.Postgresql.parser(prefixOpt = Some(s"$prefix$sep"))
    }

    def parser() = {
      io.flow.registry.v0.anorm.parsers.Http.parser() |
      io.flow.registry.v0.anorm.parsers.Postgresql.parser()
    }

  }

}