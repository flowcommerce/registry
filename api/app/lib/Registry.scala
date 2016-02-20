package io.flow.registry.api.lib

import db.ApplicationsDao
import io.flow.play.clients.{MockRegistry, ProductionRegistry, Registry, RegistryApplicationProvider}
import io.flow.postgresql.Authorization
import io.flow.play.util.FlowEnvironment
import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

/**
  * Since we are the registry, we can look up app info from our local
  * database (instead of an HTTP call to ourselves).
  */
class LocalRegistry() extends RegistryApplicationProvider {

  override def getById(applicationId: String) = {
    ApplicationsDao.findById(Authorization.All, applicationId).getOrElse {
      sys.error("application[$applicationId] not found in registrydb")
    }
  }

}

class LocalRegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {

    env.mode match {
      case Mode.Prod | Mode.Dev => {
        FlowEnvironment.Current match {
          case FlowEnvironment.Production => Seq(
            bind[Registry].to[ProductionRegistry]
          )
          case FlowEnvironment.Development => Seq(
            bind[Registry].to[LocalRegistry]
          )
        }
      }
      case Mode.Test => Seq(
        bind[Registry].to[MockRegistry]
      )
    }
  }

}
