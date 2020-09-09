package routers

import models.{Author, Book}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, Injecting}

class ApiRouterSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "ApiRouter" should {

    "get the books" in {
      val books = route(app, FakeRequest(GET, "/books/list/all")).get

      status(books) mustBe OK
      contentType(books) mustBe Some("application/json")
      contentAsJson(books) mustEqual
        Json.parse(
          """[
            |{"title":"The Sorrows of Young Werther","year":1774,"author":{"name":"Johann Wolfgang von Goethe"}},
            |{"title":"Iliad","year":-8000,"author":{"name":"Homer"}},
            |{"title":"Nad Niemnem","year":1888,"author":{"name":"Eliza Orzeszkowa"}},
            |{"title":"The Colour of Magic","year":1983,"author":{"name":"Terry Pratchett"}},
            |{"title":"The Art of Computer Programming","year":1968,"author":{"name":"Donald Knuth"}},
            |{"title":"Pharaoh","year":1897,"author":{"name":"Boleslaw Prus"}}
            |]""".stripMargin)
    }

    "add a book" in {
      val book = Book("A new book", 2020, Author("John Doe"))

      val added = route(app, FakeRequest(POST, "/books/add", FakeHeaders(), Json.toJson(book).toString())).get

      status(added) mustBe CREATED
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
      contentAsJson(book) mustEqual Json.parse(""""plop"""")
    }

  }

}
