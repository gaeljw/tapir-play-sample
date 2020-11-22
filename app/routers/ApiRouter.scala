package routers

import akka.stream.Materializer
import javax.inject._
import models.Book
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.tapir.server.play._
import sttp.tapir.swagger.play.SwaggerPlay

import scala.concurrent.ExecutionContext

@Singleton
class ApiRouter @Inject()(apiController: BookController,
                          apiDocumentation: ApiDocumentation,
                          bookEndpoints: BookEndpoints)
                         (implicit val materializer: Materializer, ec: ExecutionContext) extends SimpleRouter {

  import apiDocumentation._
  import bookEndpoints._

  override def routes: Routes = {
    // Routes are partial functions
    openApiRoute
      .orElse(booksListingRoute)
      .orElse(addBookRoute)
      .orElse(getBookRoute)
  }

  private val booksListingRoute: Routes = booksListingEndpoint
    .serverLogic(_ => apiController.listBooks())
    .toRoute

  private val addBookRoute: Routes = addBookEndpoint
    .serverLogic {
      case (authenticatedContext: AuthenticatedContext, book: Book) =>
        println(s"Authenticated with ${authenticatedContext.userId}")
        apiController.addBook(book)
    }
    .toRoute

  private val getBookRoute: Routes = getBookEndpoint
    .serverLogic(apiController.getBook)
    .toRoute

  // Doc will be on /docs
  private val openApiRoute: Routes = new SwaggerPlay(openApiYml).routes

}
