package org.book.bookshop.controller.user;

import org.book.bookshop.model.User;
import org.book.bookshop.service.BookService;
import org.book.bookshop.service.CategoryService;
import org.book.bookshop.service.OrderService;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.EmployeeView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

@Controller
public class EmployeeController extends UserController {

    private final EmployeeView view;

    public EmployeeController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, EmployeeView employeeView, OrderService orderService) {
        super(bookService, loginView, service, orderService, categoryService );
        this.view = employeeView;
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input = Integer.parseInt(view.employeeOptions());
        return input;
    }
}
