package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ClientView extends UserView {

    private final Scanner scanner;

    public String clientOptions() {
        System.out.println("1. Order a book");
        System.out.println("2. View your orders");

        return scanner.nextLine();
    }

}
