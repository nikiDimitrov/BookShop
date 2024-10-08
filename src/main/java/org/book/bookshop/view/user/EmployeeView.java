package org.book.bookshop.view.user;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.showers.EmployeeOptionsShower;

import java.util.List;

public class EmployeeView extends UserView {

    public EmployeeView() {
        super();
    }

    public String employeeOptions() {
        EmployeeOptionsShower.showOptions();
        String response = scanner.nextLine().trim();
        System.out.println();
        return response;
    }

    public String askForApprovalOfOrder(Order order, List<OrderItem> orderItems) {
        System.out.printf("Order by %s:\n\n", order.getUser().getUsername());

        showAllOrderItems(orderItems);

        System.out.println("Do you approve this order? Y\\N");

        return scanner.nextLine();
    }

    public String[] chooseBooksToRestock(List<Book> books) {
        System.out.println("Which books do you want to restock?");

        showAllBooks(books, false);

        System.out.println("Type the book numbers, separated by a comma:");
        String bookIndexes = scanner.nextLine().trim();

        System.out.println("Type how many units to add, separated by a comma:");
        String addedQuantities = scanner.nextLine().trim();

        return new String[] { bookIndexes, addedQuantities };
    }


    public void finishedApprovingOrder() {
        System.out.println("Order approved!");
    }


    public void finishDiscardingOrder() {
        System.out.println("Order discarded!");
    }

    public void startRestocking() {
        System.out.println("Restocking...");
    }

    public void finishRestocking() {
        System.out.println("Restocking has finished!");
    }

    public void noOrdersFound() {
        System.out.println("No orders to approve!");
    }
}
