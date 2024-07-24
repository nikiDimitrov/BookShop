package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.DiscardedOrder;
import org.book.bookshop.showers.ClientOptionsShower;
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

    public String[] placeOrder(List<Book> books) {
        System.out.println("What book do you want to order?\n");
        showAllBooks(books, true);

        System.out.println("Pick books by number: ");
        String wantedBooks = scanner.nextLine();

        System.out.println("Pick how many units do you want for each:");
        String booksUnits = scanner.nextLine();

        return new String[] { wantedBooks, booksUnits };
    }

    public void orderingBooks() {
        System.out.println("Ordering...");
    }

    public String confirmOrder(List<Book> books) {
        showAllBooks(books, false);
        System.out.println("Are you sure you want to order these books? Y\\N\n");


        return scanner.nextLine();
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

        System.out.println("All of these orders were discarded by an employee!");
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
}
