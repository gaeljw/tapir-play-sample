package routers

import akka.stream.Materializer
import javax.inject._
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.play._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.play._
import sttp.tapir.swagger.play.SwaggerPlay

import scala.concurrent.ExecutionContext

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
      .orElse(getBookRoute)
  }

  private val booksListingRoute: Routes = booksListingEndpoint.toRoute(_ => apiController.listBooks())

  private val addBookRoute: Routes = addBookEndpoint.toRoute(apiController.addBook)

  private val getBookRoute: Routes = getBookEndpoint.toRoute(apiController.getBook)

  // Doc will be on /docs
  private val openApiRoute: Routes = new SwaggerPlay(openApiYml).routes

}
