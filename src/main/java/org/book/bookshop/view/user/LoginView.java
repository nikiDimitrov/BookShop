package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class LoginView {

    private final Scanner scanner;

    public String generalPrompt() {
        System.out.println("Hello! Would you like to login or register?");

        return scanner.nextLine().trim();
    }

    public String[] loginPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Password: ");
        String password = scanner.nextLine().trim();

        return new String[] {username, password};

    }

    public String[] registerPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Email: ");
        String email = scanner.nextLine().trim();

        System.out.println("Password: ");
        String password = scanner.nextLine().trim();

        return new String[] {username, email, password};
    }

    public void displayError(String message) {
        System.out.println(message);
    }

    public void displayRegistrationSuccess() {
        System.out.println("Successfully registered!");
    }

    public void displayLoginSuccess() {
        System.out.println("Successfully logged in!");
    }
}
