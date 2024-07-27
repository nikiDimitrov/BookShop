package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.User;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.LoginView;
import org.book.bookshop.view.user.UserView;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public abstract class UserController {

    protected final BookService bookService;
    protected final LoginView loginView;
    protected final UserService service;
    protected final OrderService orderService;
    protected final CategoryService categoryService;
    protected final OrderItemService orderItemService;

    protected UserView view;
    protected User user;

    protected String separator = ", *";

    public abstract int run(User user);

}
