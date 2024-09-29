package org.book.bookshop.view.user;

import org.book.bookshop.model.*;
import org.book.bookshop.showers.ClientOptionsShower;

import java.util.List;
import java.util.Map;

public class ClientView extends UserView {

    public ClientView() {
        super();
    }

    public String clientOptions() {
        ClientOptionsShower.showOptions();
        String response = scanner.nextLine().trim();
        System.out.println();
        return response;
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
        System.out.println("\nAre you sure you want to order these books? Y\\N\n");

        return scanner.nextLine().trim();
    }

    private void viewOrders(Map<Order, List<OrderItem>> ordersWithItems) {
        int number = 1;
        for(Map.Entry<Order, List<OrderItem>> entrySet : ordersWithItems.entrySet()) {
            List<OrderItem> orderItems = entrySet.getValue();

            System.out.printf("Order %d:\n", number++);

            showAllOrderItems(orderItems);
        }
    }

    public void viewDiscardedOrders(Map<Order, List<OrderItem>> discardedOrdersWithItems) {
        viewOrders(discardedOrdersWithItems);

        System.out.println("All of these orders were discarded by an employee! Deleting...");
    }

    public void viewActiveOrders(Map<Order, List<OrderItem>> activeOrdersWithItems) {
        viewOrders(activeOrdersWithItems);

        System.out.println("\nAll of these orders await an employee's approval!");
    }

    public void orderingBooks() {
        System.out.println("Ordering...");
    }

    public void displayOrderSuccessful() {
        System.out.println("Order was successfully placed!");
    }

    public void unitsTooHighError(Book book) {
        System.out.printf("Wanted units for %s is too high!\n", book.getName());
    }

    public void displaySuccessfullyDeletedDiscardedOrders() {
        System.out.println("Discarded orders deleted!");
    }
}
