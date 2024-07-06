package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class UserView {
    @Autowired
    private final Scanner scanner;

    //to implement admin, client and employee features
    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public String adminOptions() {
        System.out.println("1. Add an employee");
        System.out.println("2. Add a book");
        System.out.println("3. Remove a book");
        System.out.println("4. Add category/categories");
        System.out.println("0. Exit");

        return scanner.nextLine();
    }

    public int employeeOptions() {
        return 0;
    }

    public int clientOptions() {
        //do something
        return 0;
    }
}
