package org.book.bookshop.view.user;

import org.book.bookshop.model.*;
import org.book.bookshop.showers.ClientOptionsShower;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ClientView extends UserView {

    public ClientView(Scanner scanner) {
        super(scanner);
    }

    public String clientOptions() {
        ClientOptionsShower.showOptions();
        return scanner.nextLine();
    }

    public String[] placeOrder(List<Book> books) {
        System.out.println("What book do you want to order?\n");
        showAllBooks(books, true);

        System.out.println("Pick books by numbers, separated by commas: ");
        String wantedBooks = scanner.nextLine().trim();

        System.out.println("Pick how many units do you want for each, separated by commas:");
        String booksUnits = scanner.nextLine().trim();

        return new String[] { wantedBooks, booksUnits };
    }

    public String confirmOrder(List<OrderItem> orderItems) {
        showAllOrderItems(orderItems);
        System.out.println("Are you sure you want to order these books? Y\\N\n");

        return scanner.nextLine().trim();
    }

    public void viewOrders(List<Order> orders) {
        for(int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("Order %d:\n", i + 1);

            showAllOrderItems(order.getOrderItems());

            System.out.printf("Price of order is: %.2f lv.\n", order.getTotalPrice());
        }

        System.out.println("\n\nAll of these orders await an employee's approval!");
    }

    public void viewDiscardedOrders(List<DiscardedOrder> discardedOrders) {
        for(int i = 0; i < discardedOrders.size(); i++) {
            DiscardedOrder discardedOrder = discardedOrders.get(i);
            System.out.printf("Order %d:\n", i + 1);

            showAllOrderItems(discardedOrder.getOrderItems());

            System.out.printf("Price of order is: %.2f lv.\n\n", discardedOrder.getTotalPrice());
        }

        System.out.println("All of these orders were discarded by an employee! Deleting...");
    }


    public void orderingBooks() {
        System.out.println("Ordering...");
    }

    public void displayOrderSuccessful() {
        System.out.println("Order was successfully placed!");
    }

    public void startingDisplayActiveOrders() {
        System.out.println("Active orders:\n");
    }

    public void startingDisplayDiscardedOrders() {
        System.out.println("Discarded orders:\n");
    }

    public void unitsTooHighError(Book book) {
        System.out.printf("Wanted units for %s is too high!\n", book.getName());
    }

    public void displaySuccessfullyDeletedDiscardedOrders() {
        System.out.println("Discarded orders deleted!");
    }
}
