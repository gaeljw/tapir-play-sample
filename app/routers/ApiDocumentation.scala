package routers

import javax.inject.{Inject, Singleton}
import sttp.apispec.openapi.Info
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.concurrent.Future

@Singleton
class ApiDocumentation @Inject() (bookEndpoints: BookEndpoints) {

  import bookEndpoints._

  private val openApiInfo: Info = Info("Tapir Play Sample", "1.0.0")

  val openApiEndpoints: List[ServerEndpoint[Any, Future]] = SwaggerInterpreter().fromEndpoints[Future](
    List(
      booksListingEndpoint,
      booksStreamingEndpoint,
      oneOfStreamingEndpoint,
      addBookEndpoint.endpoint, // This one is a PartialServerEndpoint
      getBookEndpoint
    ),
    openApiInfo
  )

}
