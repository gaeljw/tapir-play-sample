package routers

import javax.inject.{Inject, Singleton}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.play.jsonBody
import sttp.tapir.server.PartialServerEndpoint

import scala.concurrent.Future

@Singleton
class SecuredEndpoints @Inject()(tokenAuthenticator: TokenAuthenticator) {

  private val securedWithBearerEndpoint: Endpoint[String, AuthError, Unit, Any] = endpoint
    .in(auth.bearer[String]())
    .errorOut(statusCode(StatusCode.Unauthorized))
    .errorOut(jsonBody[AuthError])

  val securedWithBearer: PartialServerEndpoint[AuthenticatedContext, Unit, AuthError, Unit, Any, Future] = securedWithBearerEndpoint
    .serverLogicForCurrent(tokenAuthenticator.authenticateToken)

}
