package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class UserView {

    protected final Scanner scanner;

    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public void awaitEnter(){
        System.out.println("Press ENTER to continue...");

        scanner.nextLine();
    }

    public void showAllBooks(List<Book> books, boolean showCategories) {
        AtomicInteger index = new AtomicInteger(1);
        books.forEach(book -> {
            if(showCategories) {
                List<Category> categories = book.getCategories();
                System.out.printf("%d. %s by %s, %d, %.2f lv., %d units - %s\n", index.getAndIncrement(), book.getName(), book.getAuthor(),
                        book.getYear(), book.getPrice(), book.getQuantity(), displayCategories(categories));
            }
            else {
                System.out.printf("%d. %s by %s, %d, %.2f lv., %d units\n", index.getAndIncrement(), book.getName(), book.getAuthor(),
                        book.getYear(), book.getPrice(), book.getQuantity());
            }
        });
    }

    public void showAllOrderItems(List<OrderItem> orderItems) {
        AtomicInteger index = new AtomicInteger(1);
        orderItems.forEach(orderItem -> {
            Book orderedBook = orderItem.getBook();

            System.out.printf("%d. %s by %s, %d, %.2f lv, %d units ordered\n", index.getAndIncrement(), orderedBook.getName(),
                    orderedBook.getAuthor(), orderedBook.getYear(), orderedBook.getPrice(), orderItem.getQuantity());
        });
    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void displayWrongOptionError() {
        System.out.println("Option is wrong! Type a number present in the option list!");
    }

    public void displayUnequalNumberOfArgumentsError() {
        System.out.println("Number of indexes and quantities are not the same! Cancelling...");
    }

    public void displayWrongIndexError() {
        System.out.println("One of the indexes is wrong! Cancelling...");
    }

    public void displayNegativeQuantityError() {
        System.out.println("One of the quantities is below zero! It's not allowed!");
    }

    public void displayExitMessage(User user) {
        System.out.printf("Goodbye, %s! Exiting...\n", user.getUsername());
    }


    private String displayCategories(List<Category> categories) {
        return String.join(", ", categories
                .stream()
                .map(Category::getName)
                .toList());
    }
}
