package routers

import models.Book
import org.apache.pekko.stream.Materializer
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.tapir.server.play.{PlayServerInterpreter, PlayServerOptions}
import sttp.tapir.swagger.SwaggerUI

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiRouter @Inject() (apiController: BookController, apiDocumentation: ApiDocumentation, bookEndpoints: BookEndpoints)(using
    val materializer: Materializer,
    ec: ExecutionContext
) extends SimpleRouter {

  import apiDocumentation._
  import bookEndpoints._

  private val playServerOptions: PlayServerOptions = PlayServerOptions.default(using materializer, ec)
  private val interpreter = PlayServerInterpreter(playServerOptions)

  override def routes: Routes = {
    // Routes are partial functions
    openApiRoute
      .orElse(booksListingRoute)
      .orElse(booksStreamingRoute)
      .orElse(oneOfStreamingRoute)
      .orElse(addBookRoute)
      .orElse(getBookRoute)
  }

  private val booksListingRoute: Routes = interpreter.toRoutes(
    booksListingEndpoint
      .serverLogic(_ => apiController.listBooks())
  )

  private val booksStreamingRoute: Routes =
    interpreter.toRoutes(booksStreamingEndpoint.serverLogic(inputSource => apiController.streamBooks(inputSource)))

  private val oneOfStreamingRoute: Routes =
    interpreter.toRoutes(oneOfStreamingEndpoint.serverLogic(acceptHeader => apiController.listBooksInStreaming(acceptHeader)))

  private val addBookRoute: Routes = interpreter.toRoutes(
    addBookEndpoint
      .serverLogic { (authenticatedContext: AuthenticatedContext) => (book: Book) =>
        {
          println(s"Authenticated with ${authenticatedContext.userId}")
          apiController.addBook(book)
        }
      }
  )

  private val getBookRoute: Routes = interpreter.toRoutes(
    getBookEndpoint
      .serverLogic(apiController.getBook)
  )

  // Doc will be on /docs
  private val openApiRoute: Routes = interpreter.toRoutes(openApiEndpoints)

}
