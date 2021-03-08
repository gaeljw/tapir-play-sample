package books.infrastructure.routers

import akka.stream.Materializer
import books.application.BookService
import books.domain.Book
import books.infrastructure.tapir.auth.AuthenticatedContext
import books.infrastructure.tapir.{ApiDocumentation, BookEndpoints}
import play.api.Logging
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.tapir.server.play._
import sttp.tapir.swagger.play.SwaggerPlay

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ApiRouter @Inject() (
    bookController: BookService,
    bookEndpoints: BookEndpoints,
    apiDocumentation: ApiDocumentation
)(implicit val m: Materializer, ec: ExecutionContext)
    extends SimpleRouter
    with Logging {

  import apiDocumentation._
  import bookEndpoints._

  private val booksRoute: Routes = PlayServerInterpreter.toRoute {
    booksEndpoint.serverLogic(_ => bookController.listBooks)
  }

  private val addBookRoute: Routes = PlayServerInterpreter.toRoute {
    addBookEndpoint.serverLogic { case (ac: AuthenticatedContext, book: Book) =>
      logger.trace(s"Authenticated with ${ac.userId}")
      bookController.addBook(book)
    }
  }

  private val getBookRoute: Routes = PlayServerInterpreter.toRoute {
    getBookEndpoint.serverLogic(bookController.getBook)
  }

  // Doc will be on /docs
  private val openApiRoute: Routes = new SwaggerPlay(openApiYml).routes

  override def routes: Routes = {
    // Routes are partial functions
    openApiRoute
      .orElse(booksRoute)
      .orElse(addBookRoute)
      .orElse(getBookRoute)
  }
}
