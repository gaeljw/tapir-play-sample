package routers

import models.Book
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import org.apache.pekko.util.ByteString
import play.api.libs.json.Json
import repositories.BookRepository
import sttp.model.{ContentTypeRange, MediaType}

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookController @Inject() (bookRepository: BookRepository)(implicit mat: Materializer, ec: ExecutionContext) {

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

  // Note: Tapir already does a check automatically against the possible output types defined in the endpoint
  // This method purpose is to extract the format to use
  // Theoretically this will always lead a Some()
  private def extractOutputFormat(accepts: Seq[ContentTypeRange], possibleFormats: Seq[MediaType]): Option[MediaType] = {
    if (accepts.isEmpty) {
      possibleFormats.headOption
    } else {
      MediaType.bestMatch(possibleFormats, accepts)
    }
  }

  def listBooksInStreaming(acceptHeader: Seq[ContentTypeRange]): Future[Either[String, Source[ByteString, Any]]] = {
    extractOutputFormat(acceptHeader, Seq(MediaType.ApplicationJson, MediaType.TextPlain)) match {
      case Some(requestedMediaType) =>
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
      case None =>
        Future.successful(Left("Unable to provide requested format"))
    }
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
