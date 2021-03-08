package books.application

import books.domain._
import books.infrastructure.tapir.auth.AuthError

import javax.inject._
import scala.concurrent.Future

@Singleton
class BookService @Inject()(bookRepository: BookRepository) {

  def listBooks: Future[Either[Unit, Seq[Book]]] =
    Future.successful(Right(bookRepository.getBooks))

  def addBook(book: Book): Future[Either[AuthError, Unit]] =
    Future.successful(Right(bookRepository.addBook(book)))

  def getBook(title: String): Future[Either[String, Book]] = Future.successful {
    bookRepository.getBooks
      .find(_.title == title)
      .toRight(s"No book with exact title $title")
  }

}
