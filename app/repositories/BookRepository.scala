package repositories

import java.util.concurrent.atomic.AtomicReference

import javax.inject.Singleton
import models.{Author, Book}

@Singleton
class BookRepository {

  private val books: AtomicReference[Seq[Book]] = new AtomicReference(
    Seq(
      Book("The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
      Book("Iliad", -8000, Author("Homer")),
      Book("Nad Niemnem", 1888, Author("Eliza Orzeszkowa")),
      Book("The Colour of Magic", 1983, Author("Terry Pratchett")),
      Book("The Art of Computer Programming", 1968, Author("Donald Knuth")),
      Book("Pharaoh", 1897, Author("Boleslaw Prus"))
    )
  )

  def getBooks(): Seq[Book] = books.get()

  def addBook(book: Book): Unit = books.getAndUpdate(books => books :+ book)

}
