package org.book.bookshop.service;

import org.book.bookshop.helpers.BookShopValidator;
import org.book.bookshop.helpers.Result;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.*;

import java.sql.SQLException;
import java.util.List;
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

    public Result<List<Book>> findAllBooks() {
        try {
            List<Book> books = bookRepository.findAll();
            if (books.isEmpty()) {
                return Result.failure("No books found!");
            }

            return Result.success(books);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching books. %s!", e.getMessage()));
        }
    }

    public Result<Book> findById(UUID id) {
        try {
            return bookRepository.findById(id)
                    .map(Result::success)
                    .orElse(Result.failure("Book not found!"));
        } catch(SQLException e) {
            return Result.failure(String.format("Database error while fetching book. %s!", e.getMessage()));
        }

    }

    public Result<Book> saveBook(String name, String author, double price, List<Category> categories, int year, int quantity) {
        Book book = BookShopValidator.isBookValid(name, author, price, categories, year, quantity);

        if(book == null) {
            return Result.failure("Invalid book data: name and author should have more than 3 characters; price, quantity, and year should be positive!");
        }

        try {
            Book savedBook = bookRepository.save(book);

            if(savedBook != null) {
                savedBook.setCategories(categories);

                booksCategoriesRepository.joinBookAndCategories(savedBook);
            }

            return Result.success(savedBook);
        }
        catch (SQLException e) {
            return Result.failure(String.format("Database error while saving book! %s!", e.getMessage()));
        }
    }

    public synchronized Result<Void> updateBookQuantity(OrderItem orderItem) {
        try {
            Book book = orderItem.getBook();
            int newQuantity = book.getQuantity() - orderItem.getQuantity();
            book.setQuantity(newQuantity);
            bookRepository.updateQuantity(book, newQuantity);

            return Result.success(null);
        }
        catch (SQLException e) {
            return Result.failure(String.format("Database error while updating book quantity. %s!", e.getMessage()));
        }

    }

    public Result<Void> restockBook(Book book, int quantityToAdd) {
        try {
            int newQuantity = book.getQuantity() + quantityToAdd;

            bookRepository.updateQuantity(book, newQuantity);

            return Result.success(null);
        }
        catch(SQLException e) {
            return Result.failure(String.format("Database error while restocking book! %s!", e.getMessage()));
        }
    }

    public synchronized Result<Void> deleteBook(Book book) {
        try {
            List<Order> ordersOfBook = orderRepository.findByBookId(book.getId());

            for (Order order : ordersOfBook) {
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                orderItemRepository.deleteInBatch(orderItems);
            }

            orderRepository.deleteAllInBatch(ordersOfBook);

            booksCategoriesRepository.deleteBookAndCategories(book);

            categoryRepository.deleteBatch(book.getCategories());

            bookRepository.delete(book);

            return Result.success(null);
        }
        catch(SQLException e) {
            return Result.failure(String.format("Database error while deleting book. %s", e.getMessage()));
        }

    }


}
