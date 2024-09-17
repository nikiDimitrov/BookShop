package org.book.bookshop.service;

import org.book.bookshop.helpers.BookShopValidator;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BooksCategoriesRepository booksCategoriesRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public BookService() {
        this.bookRepository = new BookRepository();
        this.categoryRepository = new CategoryRepository();
        this.booksCategoriesRepository = new BooksCategoriesRepository();
        this.orderRepository = new OrderRepository();
        this.orderItemRepository = new OrderItemRepository();
    }

    public List<Book> findAllBooks() throws SQLException {
        List<Book> books = bookRepository.findAll();

        if(books.isEmpty()) {
            throw new NoSuchElementException("No books found!");
        }

        return books;
    }

    public Book findById(UUID id) throws SQLException {
        return bookRepository.findById(id).stream().findFirst().orElse(null);
    }

    public Book saveBook(String name, String author, double price, List<Category> categories, int year, int quantity) throws IllegalArgumentException, SQLException {
        Book book = BookShopValidator.isBookValid(name, author, price, categories, year, quantity);

        if(book == null) {
            throw new IllegalArgumentException("Book name and author should be more than three characters and price, quantity and year should be positive!");
        }

        Book savedBook = bookRepository.save(book);

        if(savedBook != null) {
            savedBook.setCategories(categories);

            booksCategoriesRepository.joinBookAndCategories(savedBook);
        }

        return savedBook;
    }

    public synchronized void updateBookQuantity(OrderItem orderItem) throws SQLException {
        Book book = orderItem.getBook();
        int newQuantity = book.getQuantity() - orderItem.getQuantity();
        book.setQuantity(newQuantity);
        bookRepository.updateQuantity(book, newQuantity);
    }

    public void restockBook(Book book, int quantityToAdd) throws SQLException {
        int newQuantity = book.getQuantity() + quantityToAdd;

        bookRepository.updateQuantity(book, newQuantity);
    }

    public synchronized void deleteBook(Book book) throws SQLException {
        List<Order> ordersOfBook = orderRepository.findByBookId(book.getId());

        for (Order order : ordersOfBook) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            orderItemRepository.deleteInBatch(orderItems);
        }

        orderRepository.deleteAllInBatch(ordersOfBook);

        booksCategoriesRepository.deleteBookAndCategories(book);

        categoryRepository.deleteBatch(book.getCategories());

        bookRepository.delete(book);
    }


}
