package routers

import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._


object ApiDocumentation {

  import ApiEndpoints._

  private val openApiDocs: OpenAPI = List(
    booksListingEndpoint,
    addBookEndpoint,
    getBookEndpoint
  )
    .toOpenAPI("Tapir Play Sample", "1.0.0")

  val openApiYml: String = openApiDocs.toYaml

}