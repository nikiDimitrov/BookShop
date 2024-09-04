package org.book.bookshop.factory;

import org.book.bookshop.controller.user.AdminController;
import org.book.bookshop.controller.user.ClientController;
import org.book.bookshop.controller.user.EmployeeController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.model.Role;

public class UserControllerFactory {

    private final AdminController adminController;
    private final EmployeeController employeeController;
    private final ClientController clientController;

    public UserControllerFactory() {
        this.adminController = new AdminController();
        this.employeeController = new EmployeeController();
        this.clientController = new ClientController();
    }

    public UserController getController(Role role) {
        return switch (role) {
            case ADMIN -> adminController;
            case EMPLOYEE -> employeeController;
            case CLIENT -> clientController;
        };
    }
}
