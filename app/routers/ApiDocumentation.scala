package routers

import javax.inject.{Inject, Singleton}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.{Info, OpenAPI}
import sttp.tapir.openapi.circe.yaml._

@Singleton
class ApiDocumentation @Inject()(bookEndpoints: BookEndpoints) {

  import bookEndpoints._

  private val openApiInfo: Info = Info("Tapir Play Sample", "1.0.0")

  private val openApiDocs: OpenAPI = OpenAPIDocsInterpreter().toOpenAPI(
    List(
      booksListingEndpoint,
      addBookEndpoint.endpoint, // This one is a PartialServerEndpoint
      getBookEndpoint
    ),
    openApiInfo
  )

  val openApiYml: String = openApiDocs.toYaml

}
