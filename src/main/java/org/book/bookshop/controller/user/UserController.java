package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.User;
import org.book.bookshop.service.BookService;
import org.book.bookshop.service.CategoryService;
import org.book.bookshop.service.OrderService;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.LoginView;
import org.book.bookshop.view.user.UserView;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public abstract class UserController {

    protected final BookService bookService;
    protected final LoginView loginView;
    protected final UserService service;
    protected final OrderService orderService;
    protected final CategoryService categoryService;

    protected UserView view;
    protected User user;

    public abstract int run(User user);

    public void showAllBooks(boolean showCategories) {
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            view.showAllBooks(books, showCategories);
        }
        catch(NoBooksException e){
            view.displayError(e.getMessage());
        }
    }

}
