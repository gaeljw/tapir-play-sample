package routers

import akka.stream.Materializer
import javax.inject._
import models.{Author, Book}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import repositories.BookRepository
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.play._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.play._
import sttp.tapir.swagger.play.SwaggerPlay

import scala.concurrent.{ExecutionContext, Future}

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

  // TODO add examples with error handling

}

@Singleton
class ApiController @Inject()(bookRepository: BookRepository) {

  def listBooks(): Future[Either[Unit, Seq[Book]]] = {
    Future.successful(Right(bookRepository.getBooks()))
  }

  def addBook(book: Book): Future[Either[Unit, Unit]] = {
    Future.successful(Right(bookRepository.addBook(book)))
  }

}

object ApiDocumentation {

  import ApiEndpoints._

  private val openApiDocs: OpenAPI = List(booksListingEndpoint, addBookEndpoint).toOpenAPI("Tapir Play Sample", "1.0.0")

  val openApiYml: String = openApiDocs.toYaml

}

@Singleton
class ApiRouter @Inject()(apiController: ApiController)
                         (implicit val materializer: Materializer, ec: ExecutionContext) extends SimpleRouter {

  import ApiDocumentation._
  import ApiEndpoints._

  override def routes: Routes = {
    // Routes are partial functions
    openApiRoute
      .orElse(booksListingRoute)
      .orElse(addBookRoute)
  }

  private val booksListingRoute: Routes = booksListingEndpoint.toRoute(_ => apiController.listBooks())

  private val addBookRoute: Routes = addBookEndpoint.toRoute(apiController.addBook)

  // Doc will be on /docs
  private val openApiRoute: Routes = new SwaggerPlay(openApiYml).routes

}
