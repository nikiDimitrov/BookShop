package org.book.bookshop.controller;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService service;
    private final UserView view;

    private User currentUser;

    public void run() {
        String input = view.generalPrompt();

        if(input.equalsIgnoreCase("login")) {
            loginUser();

        }
        if(input.equalsIgnoreCase("register")) {
            registerUser();
        }
    }

    public void registerUser() {
        String[] registerDetails = view.registerPrompts();

        String username = registerDetails[0];
        String email = registerDetails[1];
        String password = registerDetails[2];

        try {
            service.registerUser(username, email, password);
            view.displayRegistrationSuccess();
        }
        catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
        }
    }

    public void loginUser() {
        String[] loginDetails = view.loginPrompts();

        String username = loginDetails[0];
        String password = loginDetails[1];

        try {
            currentUser = service.loginUser(username, password);
            view.displayLoginSuccess();
        }
        catch (IncorrectInputException | UserNotFoundException e) {
            view.displayError(e.getMessage());
        }
    }
}
