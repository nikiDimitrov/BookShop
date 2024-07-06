package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AdminView {

    @Autowired
    private final Scanner scanner;

    public String[] addEmployee() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        return new String[] { username, email, password };
    }
}
