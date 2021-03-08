package books.domain

import play.api.libs.json.{Format, Json}

final case class Book(title: String, year: Int, author: Author)

object Book {
  implicit val format: Format[Book] = Json.format
}
