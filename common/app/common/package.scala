package common

import java.util.concurrent.TimeoutException

import akka.pattern.CircuitBreakerOpenException
import com.gu.contentapi.client.GuardianContentApiError
import contentapi.ErrorResponseHandler.isCommercialExpiry
import conf.switches.SwitchTrait
import model.{Cached, NoCache}
import org.apache.commons.lang.exception.ExceptionUtils
import play.api.Logger
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{RequestHeader, Result}
import play.twirl.api.Html

object `package` extends implicits.Strings with implicits.Requests with play.api.mvc.Results {

  def convertApiExceptions[T](implicit request: RequestHeader,
                              log: Logger): PartialFunction[Throwable, Either[T, Result]] = {

    convertApiExceptionsWithoutEither.andThen(Right(_))
  }

  def convertApiExceptionsWithoutEither[T](implicit request: RequestHeader,
                              log: Logger): PartialFunction[Throwable, Result] = {
    case e: CircuitBreakerOpenException =>
      log.error(s"Got a circuit breaker open error while calling content api")
      NoCache(ServiceUnavailable)
    case GuardianContentApiError(404, message, _) =>
      log.info(s"Got a 404 while calling content api: $message")
      NoCache(NotFound)
    case GuardianContentApiError(410, message, errorResponse) =>
      errorResponse match {
        case Some(errorResponse) if isCommercialExpiry(errorResponse) =>
          log.info(s"Got a 410 while calling content api: $message: ${errorResponse.message}")
          Cached(60)(Ok(views.html.commercialExpired()))
        case _ =>
          log.info(s"Got a 410 while calling content api: $message")
          NoCache(Gone)
      }
    case timeout: TimeoutException =>
      log.info(s"Got a timeout while calling content api: ${timeout.getMessage}")
      NoCache(GatewayTimeout(timeout.getMessage))
    case error =>
      log.info(s"Content api exception: ${error.getMessage}")
      log.info(s"Content api stack trace: ${ExceptionUtils.getStackTrace(error)}")
      Option(error.getCause).map { cause =>
        log.info(s"Content api exception cause: ", cause)
      }
      NoCache(InternalServerError)
  }

  /*
    NOTE: The htmlResponse & jsonResponse are () => Html functions so that you do not do all the rendering twice.
          Only the once you actually render is used
   */

  def renderFormat(htmlResponse: () => Html, jsonResponse: () => Html, page: model.Page)(implicit request: RequestHeader) = Cached(page) {
    if (request.isJson)
      JsonComponent(jsonResponse())
    else
      Ok(htmlResponse())
  }

  def renderFormat(htmlResponse: () => Html, jsonResponse: () => Html, page: model.Page, switches: Seq[SwitchTrait])(implicit request: RequestHeader) = Cached(page) {
    if (request.isJson)
      JsonComponent(page, jsonResponse())
    else
      Ok(htmlResponse())
  }

  def renderFormat(htmlResponse: () => Html, jsonResponse: () => Html, cacheTime: Integer)(implicit request: RequestHeader) = Cached(cacheTime) {
    if (request.isJson)
      JsonComponent(jsonResponse())
    else
      Ok(htmlResponse())
  }

  def renderFormat(html: () => Html, cacheTime: Integer)(implicit request: RequestHeader): Result = {
    renderFormat(html, html, cacheTime)(request)
  }
}

object Reference {
  def apply(s: String) = {
    val parts = s.split("/")
    parts(0) -> parts.drop(1).mkString("/")
  }

  def toJavaScript(s: String) = {
    val (k, v) = apply(s)

    /** Yeah ... so ... in the JavaScript references are represented like this:
      *
      * "references":[{"esaFootballTeam":"/football/team/48"},{"optaFootballTournament":"5/2012"}57"} ... ]
      *
      * See for example the source of
      * http://www.theguardian.com/football/live/2014/aug/20/maribor-v-celtic-champions-league-play-off-live-report
      *
      * Seems pretty STRANGE.
      *
      * TODO: figure out if this is actually being used. If so, refactor it.
      */
    JsObject(Seq(k -> JsString(RelativePathEscaper.escapeLeadingSlashFootballPaths(v))))
  }
}
