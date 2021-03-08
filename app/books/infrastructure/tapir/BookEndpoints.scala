package books.infrastructure.tapir

import books.infrastructure.tapir.auth.{AuthError, AuthenticatedContext}
import books.domain.{Author, Book}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.server.PartialServerEndpoint

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class BookEndpoints @Inject() (securedEndpoints: SecuredEndpoints) {

  private val baseBookEndpoint = endpoint
    .tag("Books API")
    .in("books")

  private val baseSecuredBookEndpoint: PartialServerEndpoint[AuthenticatedContext, Unit, AuthError, Unit, Any, Future] =
    securedEndpoints.securedWithBearer
      .tag("Books API")
      .in("books")

  val booksEndpoint: Endpoint[Unit, Unit, Seq[Book], Any] = baseBookEndpoint.get
    .summary("List all books")
    .in("list" / "all")
    .out(jsonBody[Seq[Book]])

  private val book: Book = Book("Pride and Prejudice", 1813, Author("Jane Austen"))

  val addBookEndpoint: PartialServerEndpoint[AuthenticatedContext, Book, AuthError, Unit, Any, Future] =
    baseSecuredBookEndpoint.post
      .summary("Add a book")
      .in("add")
      .in(jsonBody[Book].description("The book to add").example(book))
      .out(statusCode(StatusCode.Created))

  val getBookEndpoint: Endpoint[String, String, Book, Any] = baseBookEndpoint.get
    .summary("Get a book (by title)")
    .in("find")
    .in(query[String]("title").description("The title to look for"))
    .out(jsonBody[Book].description("The book (if found)"))
    .errorOut(statusCode(StatusCode.NotFound))
    .errorOut(jsonBody[String].description("Error message"))

}
