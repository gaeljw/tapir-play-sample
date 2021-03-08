package books.infrastructure.tapir.auth

import play.api.libs.json.{Format, Json}

final case class AuthError(error: String)

object AuthError {
  implicit val format: Format[AuthError] = Json.format[AuthError]
}
