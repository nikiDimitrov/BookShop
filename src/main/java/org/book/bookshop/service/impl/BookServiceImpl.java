package org.book.bookshop.service.impl;

import org.book.bookshop.model.Book;
import org.book.bookshop.repository.BookRepository;
import org.book.bookshop.service.BookService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public Book loadBookByName(String name) {
        Optional<Book> book = bookRepository.findByName(name);
        return book.stream().findFirst().orElse(null);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll().stream().toList();
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findBooksByAuthor(author);
    }

    @Override
    public List<Book> getBooksByYear(int year){
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getDatePublished().getYear() == year)
                .toList();
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBookByName(String bookName) {
        bookRepository.deleteBookByName(bookName);
    }
}
