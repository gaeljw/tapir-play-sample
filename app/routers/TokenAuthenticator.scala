package routers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

case class AuthError(error: String)

object AuthError {
  implicit val format: Format[AuthError] = Json.format[AuthError]
}

case class AuthenticatedContext(userId: String)

@Singleton
class TokenAuthenticator @Inject() ()(implicit ec: ExecutionContext) {

  def authenticateToken(bearer: String): Future[Either[AuthError, AuthenticatedContext]] = {
    Future {
      if (bearer == "SecretKey") {
        Right(AuthenticatedContext("JohnDoe"))
      } else {
        Left(AuthError("Wrong bearer"))
      }
    }
  }

}
