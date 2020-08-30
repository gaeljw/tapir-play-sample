package models

import play.api.libs.json.{Format, Json}

case class Author(name: String)

object Author {
  implicit val format: Format[Author] = Json.format[Author]
}

case class Book(title: String, year: Int, author: Author)

object Book {
  implicit val format: Format[Book] = Json.format[Book]
}