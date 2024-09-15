package org.book.bookshop.client.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.*;
import org.book.bookshop.view.user.AdminView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController extends UserController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminView view;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminController(BufferedWriter out, BufferedReader in) {
        super(out, in);
        this.view = new AdminView();
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input;

        try {
            input = Integer.parseInt(view.adminOptions());
        }
        catch (NumberFormatException e) {
            return -1;
        }

        switch(input) {
            case 1 -> registerEmployee();
            case 2 -> showAllUsers();
            case 3 -> addBook();
            case 4 -> removeBook();
            case 5 -> showAllBooks(true);
            case 6 -> showAllOrders();
            default -> {
                if(input != 0) { view.displayWrongOptionError(); }
            }
        }

        return input;
    }

    public void registerEmployee() {
        JsonNode employeeDetails = loginView.registerPrompts();

        String username = employeeDetails.get("username").asText();
        String email = employeeDetails.get("email").asText();
        String password = employeeDetails.get("password").asText();

        JsonNode registerEmployeeRequest = objectMapper.createObjectNode()
                .put("action", "REGISTER_EMPLOYEE")
                .put("admin", user.getUsername())
                .put("username", username)
                .put("email", email)
                .put("password", password);

        try {
            out.write(registerEmployeeRequest.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if(status.equals("success")) {
                loginView.displayRegistrationSuccess();
            }
            else if(status.equals("failure")){
                String error = response.get("message").asText();
                loginView.displayError(error);
            }
        } catch(IOException e) {
            log.error(String.format("Communication failure with server: %s", e.getMessage()));
            view.displayError("Failed to communicate with the server. Please try again.");
        }
    }

    public void addBook() {
        JsonNode bookDetails = view.addBook();

        String name = bookDetails.get("name").asText();
        String author = bookDetails.get("author").asText();
        String priceString = bookDetails.get("price").asText();
        String categories = bookDetails.get("categories").asText();
        String yearString = bookDetails.get("year").asText();
        String quantityString = bookDetails.get("quantity").asText();

        JsonNode addingBookRequest = objectMapper.createObjectNode()
                .put("action", "ADD_BOOK")
                .put("admin", user.getUsername())
                .put("name", name)
                .put("author", author)
                .put("price", priceString)
                .put("categories", categories)
                .put("year", yearString)
                .put("quantity", quantityString);

        try {
            out.write(addingBookRequest.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());

            String status = response.get("status").asText();

            if(status.equals("success")) {
                view.displayAddingBookSuccess();
            }
            else {
                String error = response.get("message").asText();
                view.displayError(error);
            }
        } catch (IOException e){
            log.error(String.format("Failed to add book to server: %s", e.getMessage()));
            view.displayError("Failed to add book to server. Please try again.");
        }

    }

    public void removeBook() {
        try {
            List<Book> books = getAllBooks();

            String argument = view.removeBook(books);

            int index = Integer.parseInt(argument);
            Book bookToDelete = books.get(index - 1);

            String bookJson = objectMapper.writeValueAsString(bookToDelete);

            JsonNode request = objectMapper.createObjectNode()
                    .put("action", "REMOVE_BOOK")
                    .put("admin", user.getUsername())
                    .put("book", bookJson);

            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());

            if (response.get("status").asText().equals("success")) {
                view.displayDeletingBookSuccess();
            }
            else {
                view.displayError(response.get("message").asText());
            }
        } catch (RuntimeException e) {
            view.displayError("Incorrect argument!");
        } catch (IOException e) {
            view.displayError("Failed to remove book from server. Please try again.");
            log.error(String.format("Failed to remove book from server: %s", e.getMessage()));
        }
    }


    public void showAllUsers() {
        JsonNode request = objectMapper.createObjectNode()
                .put("action", "SHOW_USERS")
                .put("admin", user.getUsername());

        try {
            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if (status.equals("success")) {
                String usersJson = response.get("users").asText();
                List<User> users = objectMapper.readValue(usersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
                view.showAllUsers(users);
            }
            else {
                view.displayError(response.get("message").asText());
            }
        } catch (IOException e) {
            view.displayError("Failed to fetch users from server. Please try again.");
            log.error("Failed to fetch users from server: {}", e.getMessage());
        }
    }


    public void showAllOrders() {
        JsonNode request = objectMapper.createObjectNode()
                .put("action", "SHOW_ORDERS")
                .put("user", user.getUsername());

        try {
            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if (status.equals("success")) {
               fetchOrdersFromResponseAndView(response);
            }
            else {
                view.displayError(response.get("message").asText());
            }
        } catch (IOException e) {
            view.displayError("Failed to show all orders from the server. Please try again.");
            log.error("Failed to show all orders from the server. {}", e.getMessage());
        }
    }

    public void showAllBooks(boolean showCategories) {
        try {
            List<Book> books = getAllBooks();
            if (books != null) {
                view.showAllBooks(books, showCategories);
            }
        } catch (NoBooksException e) {
            view.displayError(e.getMessage());
        } catch (IOException e) {
            view.displayError("Failed to fetch books from the server. Please try again.");
            log.error("Failed to fetch books from the server: {}", e.getMessage());
        }
    }

    private void fetchOrdersFromResponseAndView(JsonNode response) throws IOException {
        String ordersJson = response.get("orders").asText();
        List<Order> orders = objectMapper.readValue(ordersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        Map<Order, List<OrderItem>> ordersWithItems = new HashMap<>();
        for (Order order : orders) {
            String orderItemsJson = response.get("order_items_" + order.getId()).asText();
            List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class));
            ordersWithItems.put(order, orderItems);
        }

        view.showAllOrders(ordersWithItems);
    }
}
