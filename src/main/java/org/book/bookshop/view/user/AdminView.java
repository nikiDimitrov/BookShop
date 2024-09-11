package org.book.bookshop.view.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.showers.AdminOptionsShower;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminView extends UserView {

    public AdminView() {
        super();
    }

    public String adminOptions() {
        AdminOptionsShower.showOptions();
        return scanner.nextLine().trim();
    }

    public void showAllUsers(List<User> users){
        System.out.println("List of all users:\n");

        AtomicInteger index = new AtomicInteger(1);
        users.forEach(user -> System.out.printf("%d. %s %s %s\n", index.getAndIncrement(), user.getUsername(),
                user.getEmail(), user.getRole()));
        System.out.println();
    }

    public JsonNode addBook() {
        System.out.println("Name of book: ");
        String name = scanner.nextLine().trim();

        System.out.println("Author: ");
        String author = scanner.nextLine().trim();

        System.out.println("Price: ");
        String price = scanner.nextLine().trim();

        System.out.println("Categories, separated by commas: ");

        String categoriesString = scanner.nextLine().trim();

        System.out.println("Year: ");
        String year = scanner.nextLine().trim();

        System.out.println("Quantity: ");
        String quantity = scanner.nextLine().trim();

        return new ObjectMapper().createObjectNode()
                .put("action", "ADD_BOOK")
                .put("name", name)
                .put("author", author)
                .put("price", price)
                .put("categories", categoriesString)
                .put("year", year)
                .put("quantity", quantity);
    }

    public String removeBook(List<Book> books) {
        showAllBooks(books, false);
        System.out.println("Type number of book to delete: ");

        return scanner.nextLine().trim();
    }

    public void showAllOrders(Map<Order, List<OrderItem>> ordersWithItems) {
        if(ordersWithItems.isEmpty()) {
            System.out.println("No orders found!");
        }

        else {
            System.out.println("All of the pending orders in the system are:\n");

            AtomicInteger index = new AtomicInteger(1);
            ordersWithItems.forEach((order, orderItems) -> {
                System.out.printf("Order %d by %s:\n", index.getAndIncrement(), order.getUser().getUsername());
                showAllBooks(orderItems.stream().map(OrderItem::getBook).toList(), false);
                System.out.printf("Price of this order is: %.2f lv.\n", order.getTotalPrice());
                System.out.printf("Status is: %s\n\n", order.getStatus().getName());
            });
        }

    }

    public void displayAddingBookSuccess() {
        System.out.println("Book successfully added!");
    }

    public void displayDeletingBookSuccess() {
        System.out.println("Book successfully deleted!");
    }
}
