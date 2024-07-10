package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> findAllBooks() throws NoBooksException {
        List<Book> books = bookRepository.findAll();

        if(books.isEmpty()) {
            throw new NoBooksException("No books found!");
        }

        return bookRepository.findAll();
    }

    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findBooksByAuthor(author);
    }

    public List<Book> findBooksByName(String bookName) {
        return bookRepository.findBooksByName(bookName);
    }

    public List<Book> getBooksByYear(int year) {
        return bookRepository.findBooksByYear(year);
    }

    public Book saveBook(String name, String author, double price, List<Category> categories, int year) {
        Book book = new Book(name, author, price, categories, year);

        return bookRepository.save(book);
    }

    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

}
