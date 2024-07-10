package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;


@Component
@RequiredArgsConstructor
public abstract class UserView {

    protected final Scanner scanner;

    //to implement admin, client and employee features
    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public void onExit(User user) {
        System.out.printf("Goodbye, %s! Exiting...\n", user.getUsername());
    }

    public void awaitEnter() throws IOException {
        System.out.println("Press ENTER to continue...");

        try {
            int read = System.in.read(new byte[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

}
