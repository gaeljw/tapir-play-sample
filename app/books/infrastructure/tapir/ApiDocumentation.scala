package books.infrastructure.tapir

import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._

import javax.inject.{Inject, Singleton}

@Singleton
class ApiDocumentation @Inject() (bookEndpoints: BookEndpoints) {

  import bookEndpoints._

  private val openApiDocs: OpenAPI = {
    val es = List(
      booksEndpoint,
      addBookEndpoint.endpoint, // This one is a PartialServerEndpoint
      getBookEndpoint
      )

    OpenAPIDocsInterpreter.toOpenAPI(es, "Tapir Play Sample", "1.0.0")
  }

  val openApiYml: String = openApiDocs.toYaml
}
