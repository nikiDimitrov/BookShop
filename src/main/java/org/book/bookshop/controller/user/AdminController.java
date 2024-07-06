package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.AdminView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final UserService service;
    private final AdminView view;
    private final LoginView loginView;

    public void run(int input) {

        switch(input) {
            case 1 -> registerEmployee();
        }
    }

    public void registerEmployee() {
        String[] employeeDetails = view.addEmployee();

        String username = employeeDetails[0];
        String email = employeeDetails[1];
        String password = employeeDetails[2];

        try {
            service.registerUser(username, email, password, Role.EMPLOYEE);
            loginView.displayRegistrationSuccess();
        }
        catch (IllegalArgumentException e) {
            loginView.displayError(e.getMessage());
        }

    }
}
