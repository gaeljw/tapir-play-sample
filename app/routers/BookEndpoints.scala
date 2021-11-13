package routers

import akka.stream.scaladsl.Source
import akka.util.ByteString

import javax.inject.{Inject, Singleton}
import models.{Author, Book}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.server.PartialServerEndpoint
import sttp.capabilities.akka.AkkaStreams

import scala.concurrent.Future

@Singleton
class BookEndpoints @Inject() (securedEndpoints: SecuredEndpoints) {

  private val baseBookEndpoint = endpoint
    .tag("Books API")
    .in("books")

  private val baseSecuredBookEndpoint: PartialServerEndpoint[String, AuthenticatedContext, Unit, AuthError, Unit, Any, Future] =
    securedEndpoints.securedWithBearer
      .tag("Books API")
      .in("books")

  val booksListingEndpoint: PublicEndpoint[Unit, Unit, Seq[Book], Any] = baseBookEndpoint.get
    .summary("List all books")
    .in("list" / "all")
    .out(jsonBody[Seq[Book]])

  val booksStreamingEndpoint: PublicEndpoint[Source[ByteString, Any], Unit, Source[ByteString, Any], AkkaStreams] = baseBookEndpoint.post
    .summary("List all books in streaming")
    .in("stream" / "all")
    // Only for testing input streaming, doesn't make much sense otherwise
    .in(streamTextBody(AkkaStreams)(CodecFormat.Json()))
    .out(streamTextBody(AkkaStreams)(CodecFormat.Json()))

  val addBookEndpoint: PartialServerEndpoint[String, AuthenticatedContext, Book, AuthError, Unit, Any, Future] =
    baseSecuredBookEndpoint.post
      .summary("Add a book")
      .in("add")
      .in(
        jsonBody[Book]
          .description("The book to add")
          .example(Book("Pride and Prejudice", 1813, Author("Jane Austen")))
      )
      .out(statusCode(StatusCode.Created))

  val getBookEndpoint: PublicEndpoint[String, String, Book, Any] = baseBookEndpoint.get
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
