package io.flow.registry.api.lib

import db.ApplicationsDao
import io.flow.play.clients.{MockRegistry, ProductionRegistry, Registry, RegistryConstants}
import io.flow.postgresql.Authorization
import io.flow.play.util.{Config, FlowEnvironment}
import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

/**
  * Since we are the registry, we can look up app info from our local
  * database (instead of an HTTP call to ourselves).
  */
@javax.inject.Singleton
class DevelopmentRegistry @javax.inject.Inject() (
  app: play.api.Application,
  applicationsDao: ApplicationsDao
) extends LocalRegistry {

  //TODO: refactor duplicate code
  override def host(applicationId: String): String = {
    val app = applicationsDao.findById(Authorization.All, applicationId).getOrElse {
      sys.error("application[$applicationId] not found in registrydb")
    }

    val port = app.ports.headOption.getOrElse {
      sys.error(s"application[$applicationId] does not have any ports in registry")
    }

    host(applicationId, port.external)
  }

  override def host(applicationId: String, port: Long): String = {
    RegistryConstants.developmentHost(applicationId, port)
  }

}

/**
  * Since we are the registry, we can look up app info from our local
  * database (instead of an HTTP call to ourselves).
  */
@javax.inject.Singleton
class WorkstationRegistry @javax.inject.Inject() (
  app: play.api.Application,
  applicationsDao: ApplicationsDao
) extends LocalRegistry {

  //TODO: refactor duplicate code
  override def host(applicationId: String): String = {
    val app = applicationsDao.findById(Authorization.All, applicationId).getOrElse {
      sys.error("application[$applicationId] not found in registrydb")
    }

    val port = app.ports.headOption.getOrElse {
      sys.error(s"application[$applicationId] does not have any ports in registry")
    }

    host(applicationId, port.external)
  }

  override def host(applicationId: String, port: Long): String = {
    RegistryConstants.workstationHost(applicationId, port)
  }
}

trait LocalRegistry extends Registry {

  def host(applicationId: String, port: Long): String

}

class RegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {

    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[Registry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development => Seq(
            bind[Registry].to[DevelopmentRegistry]
          )
          case FlowEnvironment.Workstation => Seq(
            bind[Registry].to[WorkstationRegistry]
          )
        }
      }
      case Mode.Test => Seq(
        bind[Registry].to[MockRegistry]
      )
    }
  }

}
