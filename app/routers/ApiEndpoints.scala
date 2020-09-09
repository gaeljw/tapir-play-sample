package routers

import models.{Author, Book}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.play._

object ApiEndpoints {

  val booksListingEndpoint: Endpoint[Unit, Unit, Seq[Book], Nothing] = endpoint.get
    .tag("Books API")
    .summary("List all books")
    .in("books")
    .in("list" / "all")
    .out(jsonBody[Seq[Book]])

  val addBookEndpoint: Endpoint[Book, Unit, Unit, Nothing] = endpoint.post
    .tag("Books API")
    .summary("Add a book")
    .in("books")
    .in("add")
    .in(
      jsonBody[Book]
        .description("The book to add")
        .example(Book("Pride and Prejudice", 1813, Author("Jane Austen")))
    )
    .out(statusCode(StatusCode.Created))

  val getBookEndpoint: Endpoint[String, String, Book, Nothing] = endpoint.get
    .tag("Books API")
    .summary("Get a book (by title)")
    .in("books")
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
