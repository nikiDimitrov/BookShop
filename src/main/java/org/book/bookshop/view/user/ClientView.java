package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.OrderItem;
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

    public void displayOrderSuccessful() {
        System.out.println("Order was successfully placed!");
    }

    public void viewOrders(List<Order> orders) {
        System.out.println("Your orders: ");

        for(int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("Order %d:\n", i + 1);
            showAllBooks(order.getOrderItems().stream().map(OrderItem::getBook).toList(), false);
            System.out.printf("Price of order is: %.2f lv.\n", order.getTotalPrice());
        }

        System.out.println("\n\nAll of these orders await an employee's approval!");
    }
}
