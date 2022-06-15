package routers

import javax.inject.{Inject, Singleton}
import sttp.apispec.openapi.circe.yaml._
import sttp.apispec.openapi.{Info, OpenAPI}
import sttp.tapir.docs.openapi._

@Singleton
class ApiDocumentation @Inject() (bookEndpoints: BookEndpoints) {

  import bookEndpoints._

  private val openApiInfo: Info = Info("Tapir Play Sample", "1.0.0")

  private val openApiDocs: OpenAPI = OpenAPIDocsInterpreter().toOpenAPI(
    List(
      booksListingEndpoint,
      booksStreamingEndpoint,
      oneOfStreamingEndpoint,
      addBookEndpoint.endpoint, // This one is a PartialServerEndpoint
      getBookEndpoint
    ),
    openApiInfo
  )

  val openApiYml: String = openApiDocs.toYaml

}
