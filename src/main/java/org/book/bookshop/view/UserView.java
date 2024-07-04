package org.book.bookshop.view;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Data
@Component
@RequiredArgsConstructor
public class UserView {

    @Autowired
    private final UserController userController;
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        this.generalPrompt();
        String input = scanner.nextLine();

        /*
        if(input.equalsIgnoreCase("login")) {

        }*/
        if(input.equalsIgnoreCase("register")) {
            registerPrompts();;
        }


    }

    public void generalPrompt() {
        System.out.println("Hello! Would you like to login or register?");
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
        }
        catch(IllegalArgumentException e){
            displayError(e.getMessage());
        }
        finally {
            displaySuccess();
        }

    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
        System.out.println("Exiting...");
    }

    public void displaySuccess() {
        System.out.println("Successfully registered!");
    }
}
