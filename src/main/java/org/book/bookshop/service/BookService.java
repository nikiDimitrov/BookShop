package org.book.bookshop.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.BookRepository;
import org.book.bookshop.repository.BooksCategoriesRepository;
import org.book.bookshop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BooksCategoriesRepository booksCategoriesRepository;
    private final Validator validator;

    public List<Book> findAllBooks() throws NoBooksException {
        List<Book> books = bookRepository.findAll();

        if(books.isEmpty()) {
            throw new NoBooksException("No books found!");
        }

        return books;
    }

    public Book saveBook(String name, String author, double price, List<Category> categories, int year, int quantity) throws IllegalArgumentException {
        Book book = new Book(name, author, price, categories, year, quantity);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        if(!violations.isEmpty()) {
            throw new IllegalArgumentException("Book name and author should be more than three characters and price, quantity and year should be positive!");
        }

        Book savedBook = bookRepository.save(book);

        if(savedBook != null) {
            savedBook.setCategories(categories);
            booksCategoriesRepository.joinBookAndCategories(savedBook);
        }

        return savedBook;
    }

    public void updateBookQuantity(OrderItem orderItem) {
        Book book = orderItem.getBook();
        int newQuantity = book.getQuantity() - orderItem.getQuantity();
        book.setQuantity(newQuantity);
        bookRepository.updateQuantity(book, newQuantity);
    }

    public void restockBook(Book book, int quantityToAdd) {
        int newQuantity = book.getQuantity() + quantityToAdd;

        bookRepository.updateQuantity(book, newQuantity);
    }

    public void deleteBook(Book book) {
        booksCategoriesRepository.deleteBookAndCategories(book);
        categoryRepository.deleteBatch(book.getCategories());
        bookRepository.delete(book);
    }

}
