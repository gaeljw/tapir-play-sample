package routers

import javax.inject.{Inject, Singleton}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._

@Singleton
class ApiDocumentation @Inject()(bookEndpoints: BookEndpoints) {

  import bookEndpoints._

  private val openApiDocs: OpenAPI = List(
    booksListingEndpoint,
    addBookEndpoint.endpoint, // This one is a PartialServerEndpoint
    getBookEndpoint
  )
    .toOpenAPI("Tapir Play Sample", "1.0.0")

  val openApiYml: String = openApiDocs.toYaml

}