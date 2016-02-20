package io.flow.registry.api.lib

import db.ApplicationsDao

import play.api.{Environment, Configuration, Mode}
import play.api.inject.Module

import io.flow.play.clients.{MockRegistry, ProductionRegistry, Registry, RegistryConstants}
import io.flow.play.util.DefaultConfig
import io.flow.postgresql.Authorization

class LocalRegistry() extends Registry with RegistryConstants {

  override def host(applicationId: String) = {
    ApplicationsDao.findById(Authorization.All, applicationId) match {
      case None => {
        sys.error("application[$applicationId] not found in registrydb")
      }
      case Some(app) => {
        val port = app.ports.headOption.getOrElse {
          sys.error(s"application[$applicationId] does not have any ports in registry")
        }
        val host = s"http://${DevHost}:${port.external}"
        log("Development", applicationId, s"Host[$host]")
        host
      }
    }
  }

}

class LocalRegistryModule extends Module {

  def bindings(env: Environment, conf: Configuration) = {
    env.mode match {
      case Mode.Prod => Seq(
        bind[Registry].to[ProductionRegistry]
      )
      case Mode.Dev => Seq(
        bind[Registry].to[LocalRegistry]
      )
      case Mode.Test => Seq(
        bind[Registry].to[MockRegistry]
      )
    }
  }

}
