package routers

import models.{Author, Book}
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import sttp.capabilities.pekko.PekkoStreams
import sttp.model.{ContentTypeRange, StatusCode}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.play.*
import sttp.tapir.server.PartialServerEndpoint

import javax.inject.{Inject, Singleton}
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

  val booksStreamingEndpoint: PublicEndpoint[Source[ByteString, Any], Unit, Source[ByteString, Any], PekkoStreams] = baseBookEndpoint.post
    .summary("List all books in streaming")
    .in("stream" / "all")
    // Only for testing input streaming, doesn't make much sense otherwise
    .in(streamTextBody(PekkoStreams)(CodecFormat.Json()))
    .out(streamBody(PekkoStreams)(implicitly[Schema[Book]].asArray, CodecFormat.Json()))

  // Accept header is hidden in SwaggerUI because response "Content-type" can be chosen in the UI and sets it implicitly
  // This is automatic when using extractFromRequest, otherwise we could have used .schema(_.copy(hidden = true))
  private val acceptHeader: EndpointInput[Seq[ContentTypeRange]] = extractFromRequest(_.acceptsContentTypes)
    // Extract the Right part of acceptsContentTypes
    .mapDecode(accepts => DecodeResult.Value(accepts.getOrElse(Seq.empty)))(Right(_))

  val oneOfStreamingEndpoint: PublicEndpoint[Seq[ContentTypeRange], String, Source[ByteString, Any], PekkoStreams] = baseBookEndpoint.get
    .summary("List all books in streaming with format defined by Accept header")
    .in("stream" / "formatted")
    .in(acceptHeader)
    .out(
      oneOfBody(
        // Order is important: 1st one will be the default if no Accept header provided
        // It should match server implementation behavior as well
        streamBody(PekkoStreams)(implicitly[Schema[Book]].asArray, CodecFormat.Json()).toEndpointIO,
        streamTextBody(PekkoStreams)(CodecFormat.TextPlain()).toEndpointIO
      )
    )
    .errorOut(stringBody)
    .errorOut(statusCode(StatusCode.BadRequest))

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
