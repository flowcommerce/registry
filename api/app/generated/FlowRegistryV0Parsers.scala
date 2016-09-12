/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.1.16
 * apidoc:0.11.37 http://www.apidoc.me/flow/registry/0.1.16/anorm_2_x_parsers
 */
import anorm._

package io.flow.registry.v0.anorm.parsers {

  import io.flow.registry.v0.anorm.conversions.Standard._

  import io.flow.common.v0.anorm.conversions.Types._
  import io.flow.registry.v0.anorm.conversions.Types._

  object Application {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      ports = s"$prefix${sep}ports",
      dependencies = s"$prefix${sep}dependencies"
    )

    def parser(
      id: String = "id",
      ports: String = "ports",
      dependencies: String = "dependencies"
    ): RowParser[io.flow.registry.v0.models.Application] = {
      SqlParser.str(id) ~
      SqlParser.get[Seq[io.flow.registry.v0.models.Port]](ports) ~
      SqlParser.get[Seq[String]](dependencies) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      service = s"$prefix${sep}service",
      external = s"$prefix${sep}external",
      internal = s"$prefix${sep}internal",
      dependency = s"$prefix${sep}dependency"
    )

    def parser(
      id: String = "id",
      service: String = "service",
      external: String = "external",
      internal: String = "internal",
      dependency: String = "dependency"
    ): RowParser[io.flow.registry.v0.models.ApplicationForm] = {
      SqlParser.str(id) ~
      SqlParser.str(service) ~
      SqlParser.long(external).? ~
      SqlParser.long(internal).? ~
      SqlParser.get[Seq[String]](dependency).? map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      service = s"$prefix${sep}service",
      external = s"$prefix${sep}external",
      internal = s"$prefix${sep}internal",
      dependency = s"$prefix${sep}dependency"
    )

    def parser(
      service: String = "service",
      external: String = "external",
      internal: String = "internal",
      dependency: String = "dependency"
    ): RowParser[io.flow.registry.v0.models.ApplicationPutForm] = {
      SqlParser.str(service).? ~
      SqlParser.long(external).? ~
      SqlParser.long(internal).? ~
      SqlParser.get[Seq[String]](dependency).? map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      timestamp = s"$prefix${sep}timestamp",
      `type` = s"$prefix${sep}type",
      applicationPrefix = s"$prefix${sep}application"
    )

    def parser(
      id: String = "id",
      timestamp: String = "timestamp",
      `type`: String = "type",
      applicationPrefix: String = "application"
    ): RowParser[io.flow.registry.v0.models.ApplicationVersion] = {
      SqlParser.str(id) ~
      SqlParser.get[_root_.org.joda.time.DateTime](timestamp) ~
      io.flow.common.v0.anorm.parsers.ChangeType.parser(`type`) ~
      io.flow.registry.v0.anorm.parsers.Application.parserWithPrefix(applicationPrefix) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      host = s"$prefix${sep}host",
      port = s"$prefix${sep}port"
    )

    def parser(
      host: String = "host",
      port: String = "port"
    ): RowParser[io.flow.registry.v0.models.Http] = {
      SqlParser.str(host) ~
      SqlParser.long(port) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      servicePrefix = s"$prefix${sep}service",
      external = s"$prefix${sep}external",
      internal = s"$prefix${sep}internal"
    )

    def parser(
      servicePrefix: String = "service",
      external: String = "external",
      internal: String = "internal"
    ): RowParser[io.flow.registry.v0.models.Port] = {
      io.flow.registry.v0.anorm.parsers.ServiceReference.parserWithPrefix(servicePrefix) ~
      SqlParser.long(external) ~
      SqlParser.long(internal) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      dbname = s"$prefix${sep}dbname",
      host = s"$prefix${sep}host",
      port = s"$prefix${sep}port",
      user = s"$prefix${sep}user"
    )

    def parser(
      dbname: String = "dbname",
      host: String = "host",
      port: String = "port",
      user: String = "user"
    ): RowParser[io.flow.registry.v0.models.Postgresql] = {
      SqlParser.str(dbname) ~
      SqlParser.str(host) ~
      SqlParser.long(port) ~
      SqlParser.str(user) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      defaultPort = s"$prefix${sep}default_port"
    )

    def parser(
      id: String = "id",
      defaultPort: String = "default_port"
    ): RowParser[io.flow.registry.v0.models.Service] = {
      SqlParser.str(id) ~
      SqlParser.long(defaultPort) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      defaultPort = s"$prefix${sep}default_port"
    )

    def parser(
      id: String = "id",
      defaultPort: String = "default_port"
    ): RowParser[io.flow.registry.v0.models.ServiceForm] = {
      SqlParser.str(id) ~
      SqlParser.long(defaultPort) map {
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

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      defaultPort = s"$prefix${sep}default_port"
    )

    def parser(
      defaultPort: String = "default_port"
    ): RowParser[io.flow.registry.v0.models.ServicePutForm] = {
      SqlParser.long(defaultPort) map {
        case defaultPort => {
          io.flow.registry.v0.models.ServicePutForm(
            defaultPort = defaultPort
          )
        }
      }
    }

  }

  object ServiceReference {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id"
    )

    def parser(
      id: String = "id"
    ): RowParser[io.flow.registry.v0.models.ServiceReference] = {
      SqlParser.str(id) map {
        case id => {
          io.flow.registry.v0.models.ServiceReference(
            id = id
          )
        }
      }
    }

  }

  object ServiceVersion {

    def parserWithPrefix(prefix: String, sep: String = "_") = parser(
      id = s"$prefix${sep}id",
      timestamp = s"$prefix${sep}timestamp",
      `type` = s"$prefix${sep}type",
      servicePrefix = s"$prefix${sep}service"
    )

    def parser(
      id: String = "id",
      timestamp: String = "timestamp",
      `type`: String = "type",
      servicePrefix: String = "service"
    ): RowParser[io.flow.registry.v0.models.ServiceVersion] = {
      SqlParser.str(id) ~
      SqlParser.get[_root_.org.joda.time.DateTime](timestamp) ~
      io.flow.common.v0.anorm.parsers.ChangeType.parser(`type`) ~
      io.flow.registry.v0.anorm.parsers.Service.parserWithPrefix(servicePrefix) map {
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
      io.flow.registry.v0.anorm.parsers.Http.parserWithPrefix(prefix, sep) |
      io.flow.registry.v0.anorm.parsers.Postgresql.parserWithPrefix(prefix, sep)
    }

    def parser() = {
      io.flow.registry.v0.anorm.parsers.Http.parser() |
      io.flow.registry.v0.anorm.parsers.Postgresql.parser()
    }

  }

}