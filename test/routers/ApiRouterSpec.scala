package routers

import models.{Author, Book}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{DefaultBodyReadables, DefaultBodyWritables, JsonBodyReadables, WSClient}
import play.api.test.Helpers.*
import play.api.test.{FakeHeaders, FakeRequest, Injecting}

class ApiRouterSpec
    extends PlaySpec
    with GuiceOneServerPerTest
    with Injecting
    with DefaultBodyReadables
    with DefaultBodyWritables
    with JsonBodyReadables {

  implicit private val actorSystem: ActorSystem = ActorSystem("ApiRouterSpec")

  private lazy val wsClient = app.injector.instanceOf[WSClient]

  "ApiRouter" should {

    "get the books" in {
      val books = route(app, FakeRequest(GET, "/books/list/all")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("application/json")
      contentAsJson(books) mustEqual
        Json.parse("""[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}},
            |{"title":"The Colour of Magic","year":1983,"author":{"name":"Terry Pratchett"}},
            |{"title":"The Art of Computer Programming","year":1968,"author":{"name":"Donald Knuth"}},
            |{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}
            |]""".stripMargin)
    }

    "stream the books" in {
      val inputData: Source[ByteString, Any] = Source(
        Seq(
          """{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}}""",
          """{"title":"Iliad","year":-8000,"author":{"name":"Homer"}}""",
          """{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}}"""
        )
      )
        .map(str => ByteString(str))

      val books = await(wsClient.url(s"http://localhost:$port/books/stream/all").post(inputData))

      books.status mustBe OK
      books.contentType mustBe "application/json"
      books.body[JsValue] mustEqual
        Json.parse("""[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}}
            |]""".stripMargin)
    }

    "stream the books depending on the requested format - JSON" in {
      val books = route(app, FakeRequest(GET, "/books/stream/formatted").withHeaders("Accept" -> "application/json")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("application/json")
      contentAsJson(books) mustEqual
        Json.parse("""[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}},
            |{"title":"The Colour of Magic","year":1983,"author":{"name":"Terry Pratchett"}},
            |{"title":"The Art of Computer Programming","year":1968,"author":{"name":"Donald Knuth"}},
            |{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}
            |]""".stripMargin)

    }

    "stream the books depending on the requested format - Text" in {
      val books = route(app, FakeRequest(GET, "/books/stream/formatted").withHeaders("Accept" -> "text/plain")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("text/plain")
      contentAsString(books) mustEqual
        """title=The Sorrows of Young Werther;year=1774
          |title=Iliad;year=-8000
          |title=Nad Niemnem;year=1888
          |title=The Colour of Magic;year=1983
          |title=The Art of Computer Programming;year=1968
          |title=Pharaoh;year=1897""".stripMargin

    }

    "stream the books depending on the requested format - JSON because of preference order" in {
      val books = route(app, FakeRequest(GET, "/books/stream/formatted").withHeaders("Accept" -> "application/json,text/plain")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("application/json")
      contentAsJson(books) mustEqual
        Json.parse("""[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}},
            |{"title":"The Colour of Magic","year":1983,"author":{"name":"Terry Pratchett"}},
            |{"title":"The Art of Computer Programming","year":1968,"author":{"name":"Donald Knuth"}},
            |{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}
            |]""".stripMargin)

    }

    "stream the books depending on the requested format - Text because of weight in header" in {
      val books = route(app, FakeRequest(GET, "/books/stream/formatted").withHeaders("Accept" -> "application/json;q=0.9,text/plain")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("text/plain")
      contentAsString(books) mustEqual
        """title=The Sorrows of Young Werther;year=1774
          |title=Iliad;year=-8000
          |title=Nad Niemnem;year=1888
          |title=The Colour of Magic;year=1983
          |title=The Art of Computer Programming;year=1968
          |title=Pharaoh;year=1897""".stripMargin
    }

    "stream the books depending on the requested format - JSON because of preference in code" in {
      val books = route(app, FakeRequest(GET, "/books/stream/formatted").withHeaders("Accept" -> "")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("application/json")
      contentAsJson(books) mustEqual
        Json.parse("""[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}},
            |{"title":"The Colour of Magic","year":1983,"author":{"name":"Terry Pratchett"}},
            |{"title":"The Art of Computer Programming","year":1968,"author":{"name":"Donald Knuth"}},
            |{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}
            |]""".stripMargin)

    }

    "add a book with valid authentication" in {
      val book = Book("A new book", 2020, Author("John Doe"))

      val added = route(
        app,
        FakeRequest(POST, "/books/add", FakeHeaders(Seq("Authorization" -> "Bearer SecretKey")), Json.toJson(book).toString())
      ).get

      status(added) mustBe CREATED
    }

    "add a book with invalid authentication" in {
      val book = Book("A new book", 2020, Author("John Doe"))

      val added =
        route(app, FakeRequest(POST, "/books/add", FakeHeaders(Seq("Authorization" -> "Bearer BadKey")), Json.toJson(book).toString())).get

      status(added) mustBe UNAUTHORIZED
    }

    "get a book by title" in {
      val book = route(app, FakeRequest(GET, "/books/find?title=Pharaoh")).get

      status(book) mustBe OK
      contentType(book) mustBe Some("application/json")
      contentAsJson(book) mustEqual Json.parse("""{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}""")
    }

    "get a book by title - not found" in {
      val book = route(app, FakeRequest(GET, "/books/find?title=ThisOneDoesNotExist")).get

      status(book) mustBe NOT_FOUND
      contentType(book) mustBe Some("application/json")
      contentAsJson(book) mustEqual Json.parse(""""No book with exact title ThisOneDoesNotExist"""")
    }

  }

}
