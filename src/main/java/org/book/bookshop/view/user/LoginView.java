package org.book.bookshop.view.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public class LoginView {

    private final Scanner scanner;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginView() {
        this.scanner = new Scanner(System.in);
    }

    public String generalPrompt() {
        System.out.println("Hello! Would you like to login or register?");

        return scanner.nextLine().trim();
    }

    public JsonNode loginPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Password: ");
        String password = scanner.nextLine().trim();

        return objectMapper.createObjectNode()
                .put("action", "LOGIN")
                .put("username", username)
                .put("password", password);

    }

    public JsonNode registerPrompts() {
        System.out.println("Username: ");
        String username = scanner.nextLine().trim();

        System.out.println("Email: ");
        String email = scanner.nextLine().trim();

        System.out.println("Password: ");
        String password = scanner.nextLine().trim();

        return objectMapper.createObjectNode()
                .put("action", "REGISTER")
                .put("username", username)
                .put("email", email)
                .put("password", password);
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
