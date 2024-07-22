package org.book.bookshop.controller.user;

import org.book.bookshop.model.User;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.EmployeeView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

@Controller
public class EmployeeController extends UserController {

    private final EmployeeView view;

    public EmployeeController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, EmployeeView employeeView, OrderItemService orderItemService, OrderService orderService) {
        super(bookService, loginView, service, orderService, categoryService, orderItemService);
        this.view = employeeView;
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input = Integer.parseInt(view.employeeOptions());
        return input;
    }
}
