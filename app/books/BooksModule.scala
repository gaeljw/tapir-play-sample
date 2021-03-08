package books

import books.domain.BookRepository
import books.infrastructure.data.AtomicBookRepository
import net.codingwell.scalaguice.ScalaModule

import javax.inject.Singleton

final class BooksModule extends ScalaModule {
  override def configure(): Unit = {
    bind[BookRepository].to[AtomicBookRepository].in[Singleton]()
  }
}
