package org.book.bookshop.controller.user;

import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.User;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.LoginView;
import org.book.bookshop.view.user.UserView;

import java.util.Comparator;
import java.util.List;

public abstract class UserController {

    protected final BookService bookService;
    protected final LoginView loginView;
    protected final UserService service;
    protected final OrderService orderService;
    protected final CategoryService categoryService;
    protected final OrderItemService orderItemService;
    protected final StatusService statusService;

    protected UserView view;
    protected User user;

    protected static final String SEPARATOR = ", *";

    protected UserController() {
        this.bookService = new BookService();
        this.loginView = new LoginView();
        this.service = new UserService();
        this.orderService = new OrderService();
        this.categoryService = new CategoryService();
        this.orderItemService = new OrderItemService();
        this.statusService = new StatusService();
    }

    public abstract int run(User user);

    protected List<Book> getAllBooks() throws NoBooksException {
        List<Book> books = bookService.findAllBooks();

        return books.stream()
                .sorted(Comparator.comparing(Book::getName))
                .toList();
    }
}
