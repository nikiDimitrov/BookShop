package org.book.bookshop.controller.user;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private final UserService service;
    private final LoginView view;

    private User currentUser;

    public User run() {
        String input = view.generalPrompt();

        if(input.equalsIgnoreCase("login")) {
            loginUser();

        }
        if(input.equalsIgnoreCase("register")) {
            registerUser();
        }

        return currentUser;
    }

    public void registerUser() {
        String[] registerDetails = view.registerPrompts();

        String username = registerDetails[0];
        String email = registerDetails[1];
        String password = registerDetails[2];

        try {
            currentUser = service.registerUser(username, email, password, Role.CLIENT);
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
