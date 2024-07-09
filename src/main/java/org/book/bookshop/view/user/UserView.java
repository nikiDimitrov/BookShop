package org.book.bookshop.view.user;

import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;


@Component
public abstract class UserView {

    //to implement admin, client and employee features
    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public void onExit(User user) {
        System.out.printf("Goodbye, %s! Exiting...\n", user.getUsername());
    }

}
