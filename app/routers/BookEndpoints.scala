package routers

import models.{Author, Book}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.play._

object BookEndpoints {

  private val baseBookEndpoint = endpoint
    .tag("Books API")
    .in("books")

  val booksListingEndpoint: Endpoint[Unit, Unit, Seq[Book], Nothing] = baseBookEndpoint.get
    .summary("List all books")
    .in("list" / "all")
    .out(jsonBody[Seq[Book]])

  val addBookEndpoint: Endpoint[Book, Unit, Unit, Nothing] = baseBookEndpoint.post
    .summary("Add a book")
    .in("add")
    .in(
      jsonBody[Book]
        .description("The book to add")
        .example(Book("Pride and Prejudice", 1813, Author("Jane Austen")))
    )
    .out(statusCode(StatusCode.Created))

  val getBookEndpoint: Endpoint[String, String, Book, Nothing] = baseBookEndpoint.get
    .summary("Get a book (by title)")
    .in("find")
    .in(
      query[String]("title")
        .description("The title to look for")
    )
    .out(
      jsonBody[Book]
        .description("The book (if found)")
    )
    .errorOut(
      statusCode(StatusCode.NotFound)
    )
    .errorOut(
      jsonBody[String]
        .description("Error message")
    )

}
