package org.book.bookshop.repository;

import org.book.bookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByName(String name);
    List<Book> findBooksByAuthor(String author);
    void deleteBookByName(String name);
}
