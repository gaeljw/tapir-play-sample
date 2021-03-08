package books.infrastructure.data

import books.domain._

import java.util.concurrent.atomic.AtomicReference
import javax.inject.Singleton

@Singleton
class AtomicBookRepository extends BookRepository {

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

  override def getBooks: Seq[Book] = books.get()

  override def addBook(book: Book): Unit = books.getAndUpdate(books => books :+ book)

}
