package db

import anorm._
import io.flow.log.RollbarLogger
import play.api.db.Database

import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class HealthchecksDao @javax.inject.Inject() (
  db: Database,
  logger: RollbarLogger
) {

  private[this] val Query = "select 1 as num"

  def isHealthy(): Boolean = {
    Try {
      db.withConnection { implicit c =>
        SQL(Query).as(SqlParser.long("num").*).headOption.getOrElse {
          sys.error(s"Query[$Query] returned no results")
        }
      }
    } match {
      case Success(_) => true
      case Failure(ex) => {
        logger.error("DB healthcheck query failed", ex)
        false
      }
    }
  }
}
