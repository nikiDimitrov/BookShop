package org.book.bookshop.factory;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.user.AdminController;
import org.book.bookshop.controller.user.ClientController;
import org.book.bookshop.controller.user.EmployeeController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserControllerFactory {

    private final AdminController adminController;
    private final EmployeeController employeeController;
    private final ClientController clientController;

    public UserController getController(Role role) {
        return switch (role) {
            case ADMIN -> adminController;
            case EMPLOYEE -> employeeController;
            case CLIENT -> clientController;
        };
    }
}
