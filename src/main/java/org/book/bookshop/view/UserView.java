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
    private final Scanner scanner;

    public String generalPrompt() {
        System.out.println("Hello! Would you like to login or register?");

        return scanner.nextLine();
    }

    public String[] loginPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        return new String[] {username, password};

    }

    public String[] registerPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        return new String[] {username, email, password};
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
