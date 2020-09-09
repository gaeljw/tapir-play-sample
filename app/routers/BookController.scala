package routers

import javax.inject._
import models.Book
import repositories.BookRepository

import scala.concurrent.Future


@Singleton
class BookController @Inject()(bookRepository: BookRepository) {

  def listBooks(): Future[Either[Unit, Seq[Book]]] = {
    Future.successful(Right(bookRepository.getBooks()))
  }

  def addBook(book: Book): Future[Either[Unit, Unit]] = {
    Future.successful(Right(bookRepository.addBook(book)))
  }

  def getBook(title: String): Future[Either[String, Book]] = {
    Future.successful {
      bookRepository.getBooks()
        .find(_.title == title)
        .toRight(s"No book with exact title $title")
    }
  }

}