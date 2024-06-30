package org.book.bookshop.repository;

import org.book.bookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findBooksByAuthor(String author);
    List<Book> findBooksByYear(int year);
    List<Book> findBooksByName(String bookName);
}
