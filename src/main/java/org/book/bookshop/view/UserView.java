package org.book.bookshop.view;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.UserController;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Data
@Component
@RequiredArgsConstructor
public class UserView {

    @Autowired
    private final UserController userController;

    private User currentUser;

    private Scanner scanner = new Scanner(System.in);

    public void run() {
        this.generalPrompt();
        String input = scanner.nextLine();
        if(input.equalsIgnoreCase("login")) {
            loginPrompts();

        }
        if(input.equalsIgnoreCase("register")) {
            registerPrompts();;
        }
    }

    public void generalPrompt() {
        System.out.println("Hello! Would you like to login or register?");
    }

    public void loginPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = userController.loginUser(username, password);
            displayLoginSuccess();
        }
        catch (IncorrectInputException | UserNotFoundException e) {
            displayError(e.getMessage());
        }


    }
    public void registerPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        try {
            userController.insertUser(username, email, password);
            displayRegistrationSuccess();
        }
        catch(IllegalArgumentException e){
            displayError(e.getMessage());
        }

    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void displayRegistrationSuccess() {
        System.out.println("Successfully registered!");
    }

    public void displayLoginSuccess() {
        System.out.println("Successfully logged in!");
    }
}
