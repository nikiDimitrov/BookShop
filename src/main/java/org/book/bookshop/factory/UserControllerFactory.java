package org.book.bookshop.factory;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.user.AdminController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserControllerFactory {

    @Autowired
    private final AdminController adminController;

    //@Autowired
    //private EmployeeController employeeController;

    //@Autowired
    //private ClientController clientController;

    public UserController getController(User user) {
        Role role = user.getRole();
        return switch (role) {
            case ADMIN -> adminController;
            default -> null;
            //case EMPLOYEE -> employeeController;
            //case CLIENT -> clientController;
        };
    }
}
