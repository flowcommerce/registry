package utils

import akka.stream.Materializer
import io.flow.log.RollbarLogger
import io.flow.util.Config
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.HttpFilters

/** To use in any Flow app depending on lib-play:
  *
  * (1) Add this to your base.conf: play.http.filters=utils.RegistryLoggingFilter
  */

class RegistryLoggingFilter @javax.inject.Inject() (filterImpl: RegistryLoggingFilterImpl) extends HttpFilters {
  def filters: Seq[Filter] = Seq(filterImpl)
}

class RegistryLoggingFilterImpl @javax.inject.Inject() (implicit
  ec: ExecutionContext,
  m: Materializer,
  logger: RollbarLogger,
  config: Config
) extends Filter {

  private val LoggedRequestMethodConfig = "play.http.logging.methods"
  private val DefaultLoggedRequestMethods = Seq("GET", "PATCH", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
  private val LoggedHeaders = Seq(
    "User-Agent",
    "X-Forwarded-For",
    "CF-Connecting-IP",
    "X-Apidoc-Version"
  ).map(_.toLowerCase)

  private val loggedRequestMethods =
    config.optionalList(LoggedRequestMethodConfig).getOrElse(DefaultLoggedRequestMethods).toSet

  def apply(f: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis
    f(requestHeader).map { result =>
      /*
       * If user defined a list of methods that produce logs, then use that
       * Otherwise default to list defined here, which is everything
       */
      if (loggedRequestMethods.contains(requestHeader.method)) {
        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime
        val headerMap = requestHeader.headers.toMap
        val requestId = headerMap.getOrElse("X-Flow-Request-Id", Nil).mkString(",")
        val line = Seq(
          requestHeader.method,
          s"${requestHeader.host}${requestHeader.uri}",
          result.header.status.toString,
          s"${requestTime}ms",
          requestId,
          headerMap.getOrElse("User-Agent", Nil).mkString(","),
          headerMap.getOrElse("X-Forwarded-For", Nil).mkString(","),
          headerMap.getOrElse("CF-Connecting-IP", Nil).mkString(",")
        ).mkString(" ")

        logger
          .withKeyValue("https", requestHeader.secure)
          .withKeyValue("version", requestHeader.version)
          .withKeyValue("method", requestHeader.method)
          .withKeyValue("host", requestHeader.host)
          .withKeyValue("path", requestHeader.path)
          .withKeyValue("query_params", requestHeader.queryString)
          .withKeyValue("http_code", result.header.status)
          .withKeyValue("request_time_ms", requestTime)
          .withKeyValue(
            "request_headers",
            headerMap
              .map { case (key, value) => key.toLowerCase -> value }
              .view
              .filterKeys(LoggedHeaders.contains)
          )
          .withKeyValue("request_id", requestId)
          .info(line)
      }

      result
    }
  }

  override implicit def mat: Materializer = m
}
