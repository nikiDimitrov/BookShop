package org.book.bookshop.service;

import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.helpers.BookShopValidator;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.BookRepository;
import org.book.bookshop.repository.BooksCategoriesRepository;
import org.book.bookshop.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BooksCategoriesRepository booksCategoriesRepository;

    public BookService() {
        this.bookRepository = new BookRepository();
        this.categoryRepository = new CategoryRepository();
        this.booksCategoriesRepository = new BooksCategoriesRepository();
    }

    public List<Book> findAllBooks() throws NoBooksException {
        List<Book> books = bookRepository.findAll();

        if(books.isEmpty()) {
            throw new NoBooksException("No books found!");
        }

        return books;
    }

    public Book findById(UUID id) {
        return bookRepository.findById(id).stream().findFirst().orElse(null);
    }

    public Book saveBook(String name, String author, double price, List<Category> categories, int year, int quantity) throws IllegalArgumentException {
        Book book = BookShopValidator.isBookValid(name, author, price, categories, year, quantity);

        if(book == null) {
            throw new IllegalArgumentException("Book name and author should be more than three characters and price, quantity and year should be positive!");
        }

        Book savedBook = bookRepository.save(book);

        if(savedBook != null) {
            savedBook.setCategories(categories);

            CompletableFuture.runAsync(() -> booksCategoriesRepository.joinBookAndCategories(savedBook));
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

        CompletableFuture<Void> deleteCategories = CompletableFuture.runAsync(
                () -> categoryRepository.deleteBatch(book.getCategories()));

        CompletableFuture<Void> deleteBook = CompletableFuture.runAsync(
                () -> bookRepository.delete(book));

        CompletableFuture<Void> allDeletes = CompletableFuture.allOf(deleteCategories, deleteBook);

        allDeletes.exceptionally(ex -> {
            System.err.println("An error occurred during deletion: " + ex.getMessage());
            return null;
        });
    }

}
