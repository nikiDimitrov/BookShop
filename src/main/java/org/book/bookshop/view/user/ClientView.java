package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.constants.ClientOptionsShower;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ClientView extends UserView {

    private final Scanner scanner;

    public String clientOptions() {
        ClientOptionsShower.showOptions();
        return scanner.nextLine();
    }

    public String placeOrder(List<Book> books) {
        System.out.println("What book do you want to order?\n");
        showAllBooks(books, true);

        System.out.println("Pick book by number: ");

        return scanner.nextLine();
    }

    public void orderingBook(Book book) {
        System.out.printf("Ordering %s...\n", book.getName());
    }

    public String confirmOrder(Book book) {
        System.out.printf("Are you sure you want to order %s? Y\\N\n", book.getName());

        return scanner.nextLine();
    }

    public void displayOrderSuccessful() {
        System.out.println("Order was successfully placed!");
    }

    public void viewOrders(List<Order> orders) {
        System.out.println("Your orders: ");

        for(int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("Order %d:\n", i + 1);
            showAllBooks(order.getBooks(), false);
        }

        System.out.println("\nAll of these orders await an employee's approval!");
    }
}
