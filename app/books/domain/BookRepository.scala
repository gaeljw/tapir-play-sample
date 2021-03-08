package books.domain

trait BookRepository {

  def getBooks: Seq[Book]

  def addBook(book: Book): Unit

}
