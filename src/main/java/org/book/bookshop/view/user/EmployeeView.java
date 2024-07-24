package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.showers.EmployeeOptionsShower;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class EmployeeView extends UserView {

    private final Scanner scanner;

    public String employeeOptions() {
        EmployeeOptionsShower.showOptions();
        return scanner.nextLine();
    }

    public String askForApprovalOfOrder(Order order) {
        System.out.printf("Order by %s:\n\n", order.getUser().getUsername());

        List<Book> books  = order.getOrderItems()
                .stream()
                .map(OrderItem::getBook)
                .toList();

        showAllBooks(books, false);

        System.out.println("Do you approve this order? Y\\N");

        return scanner.nextLine();
    }

    public void startApprovingOrders() {
        System.out.println("Listing orders one by one...\n");
    }

    public void startApprovingOrder() {
        System.out.println("Approving order...");
    }

    public void finishedApprovingOrder() {
        System.out.println("Order approved!");
    }

    public void startDiscardingOrder() {
        System.out.println("Discarding order...");
    }

    public void finishDiscardingOrder() {
        System.out.println("Order discarded!");
    }

    public void noOrdersFound() {
        System.out.println("No orders to approve!");
    }
}
