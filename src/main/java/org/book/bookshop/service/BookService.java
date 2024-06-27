package org.book.bookshop.service;

import org.book.bookshop.model.Book;

import java.time.LocalDateTime;
import java.util.List;

public interface BookService {
    Book loadBookByName(String name);
    List<Book> findAllBooks();
    List<Book> findBooksByAuthor(String author);
    List<Book> getBooksByYear(int year);
    Book saveBook(Book book);
    void deleteBookByName(String name);
}
