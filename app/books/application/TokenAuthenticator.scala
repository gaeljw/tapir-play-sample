package books.application

import books.infrastructure.tapir.auth.{AuthError, AuthenticatedContext}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TokenAuthenticator @Inject() ()(implicit ec: ExecutionContext) {

  def authenticateToken(bearer: String): Future[Either[AuthError, AuthenticatedContext]] = Future {
    if (bearer == "SecretKey") Right(AuthenticatedContext("JohnDoe")) else Left(AuthError("Wrong bearer"))
  }

}
