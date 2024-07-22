package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.showers.AdminOptionsShower;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AdminView extends UserView {

    private final Scanner scanner;

    public String adminOptions() {
        AdminOptionsShower.showOptions();
        return scanner.nextLine();
    }

    public void showAllUsers(List<User> users){
        System.out.println("List of all users:\n");

        for(int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.printf("%d. %s %s %s\n", i + 1, user.getUsername(),
                    user.getEmail(), user.getRole());
        }

        System.out.println();
    }
    
    public String[] addBook() {
        System.out.println("Name of book: ");
        String name = scanner.nextLine();

        System.out.println("Author: ");
        String author = scanner.nextLine();

        System.out.println("Price: ");
        String price = scanner.nextLine();

        System.out.println("Categories (with , in between): ");

        String categoriesString = scanner.nextLine();

        System.out.println("Year: ");
        String year = scanner.nextLine();

        System.out.println("Quantity: ");
        String quantity = scanner.nextLine();

        return new String[] { name, author, price, categoriesString, year, quantity };
    }

    public String removeBook(List<Book> books) {
        showAllBooks(books, false);
        System.out.println("Type number of book to delete: ");

        return scanner.nextLine();
    }

    public void showAllOrders(List<Order> orders) {
        System.out.println("All of the pending orders in the system are:\n");

        for(int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("Order %d by %s:\n", i + 1, order.getUser().getUsername());
            showAllBooks(order.getOrderItems().stream().map(OrderItem::getBook).toList(), false);
            System.out.printf("Price of this order is: %.2f lv.\n\n", order.getTotalPrice());
        }
    }

    public void displayAddingBookSuccess() {
        System.out.println("Book successfully added!");
    }

    public void displayDeletingBookSuccess() {
        System.out.println("Book successfully deleted!");
    }
}
