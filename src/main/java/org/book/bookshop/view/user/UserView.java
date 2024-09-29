package org.book.bookshop.view.user;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.OrderItem;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class UserView {

    protected final Scanner scanner;

    public UserView() {
        this.scanner = new Scanner(System.in);
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
        double totalPrice = 0;
        AtomicInteger index = new AtomicInteger(1);

        for(OrderItem orderItem : orderItems) {
            Book orderedBook = orderItem.getBook();

            System.out.printf("%d. %s by %s, %d, %.2f lv, %d units ordered\n", index.getAndIncrement(), orderedBook.getName(),
                    orderedBook.getAuthor(), orderedBook.getYear(), orderedBook.getPrice(), orderItem.getQuantity());

            totalPrice += orderItem.getQuantity() * orderedBook.getPrice();
        }

        System.out.printf("Total price is: %.2f lv.\n", totalPrice);
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

    private String displayCategories(List<Category> categories) {
        return String.join(", ", categories
                .stream()
                .map(Category::getName)
                .toList());
    }
}
