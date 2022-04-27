package routers

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import models.Book
import play.api.libs.json.Json
import repositories.BookRepository
import sttp.model.MediaType

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookController @Inject()(bookRepository: BookRepository)(implicit mat: Materializer, ec: ExecutionContext) {

  def listBooks(): Future[Either[Unit, Seq[Book]]] = {
    Future.successful(Right(bookRepository.getBooks()))
  }

  def streamBooks(inputSource: Source[ByteString, Any]): Future[Either[Unit, Source[ByteString, Any]]] = {
    val inputBooks = inputSource
      .map(bs => bs.decodeString("UTF-8"))
      .map(str => Json.parse(str).as[Book])
      .runWith(Sink.seq)

    inputBooks.map { books =>
      val source = Source(books)
        .map(b => Json.toJson(b).toString())
        .intersperse("[", ", ", "]")
        .map(str => ByteString(str))
      Right(source)
    }
  }

  def listBooksInStreaming(acceptHeader: String): Future[Either[Unit, Source[ByteString, Any]]] = {
    val requestedMediaType: MediaType = MediaType.unsafeParse(acceptHeader)
    val source = if (requestedMediaType == MediaType.TextPlain) {
      Source(bookRepository.getBooks())
        .map(b => s"title=${b.title};year=${b.year}")
        .intersperse("\n")
        .map(str => ByteString(str))
    } else {
      Source(bookRepository.getBooks())
        .map(b => Json.toJson(b).toString())
        .intersperse("[", ", ", "]")
        .map(str => ByteString(str))
    }
    Future.successful(Right(source))
  }

  def addBook(book: Book): Future[Either[AuthError, Unit]] = {
    Future.successful(Right(bookRepository.addBook(book)))
  }

  def getBook(title: String): Future[Either[String, Book]] = {
    Future.successful {
      bookRepository
        .getBooks()
        .find(_.title == title)
        .toRight(s"No book with exact title $title")
    }
  }

}
