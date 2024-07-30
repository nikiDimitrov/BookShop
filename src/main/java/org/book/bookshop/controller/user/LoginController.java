package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.book.bookshop.exceptions.NoUsersException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService service;
    private final LoginView view;
    private static final Object lock = new Object();

    private User currentUser;

    public User run() {
        String input = view.generalPrompt();

        switch (input.toLowerCase()) {
            case "login" -> loginUser();
            case "register" -> registerUser();
            default -> view.displayError("Type login or register!");
        }

        return currentUser;
    }

    public void registerUser() {
        String[] registerDetails = view.registerPrompts();

        String username = registerDetails[0];
        String email = registerDetails[1];
        String password = registerDetails[2];

        synchronized (lock) {
            try {
                currentUser = service.registerUser(username, email, password, Role.CLIENT);
                view.displayRegistrationSuccess();
            } catch (IllegalArgumentException e) {
                view.displayError(e.getMessage());
            }
        }
    }

    public void loginUser() {
        String[] loginDetails = view.loginPrompts();

        String username = loginDetails[0];
        String password = loginDetails[1];

        synchronized (lock) {
            try {
                currentUser = service.loginUser(username, password);
                view.displayLoginSuccess();
            } catch (IncorrectInputException | UserNotFoundException
                     | NoUsersException e) {
                view.displayError(e.getMessage());
            }
        }
    }
}
