package org.book.bookshop.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.exceptions.NoUsersException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.helpers.StatusHelper;
import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler extends Thread {
    private static final String SEPARATOR = ", *";

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private BufferedReader in;
    private BufferedWriter out;
    private final Socket clientSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String requestString = in.readLine();
            while(true) {
                JsonNode request = objectMapper.readTree(requestString);

                if(request.get("action").asText().equals("EXIT")) {
                    break;
                }

                switch(request.get("action").asText()) {
                    case "REGISTER":
                        registerUser(request);
                        break;
                    case "LOGIN":
                        loginUser(request);
                        break;
                    case "REGISTER_EMPLOYEE":
                        registerEmployee(request);
                        break;
                    case "ADD_BOOK":
                        addBook(request);
                        break;
                    case "SHOW_BOOKS":
                        showAllBooks();
                        break;
                    case "REMOVE_BOOK":
                        removeBook(request);
                        break;
                    case "SHOW_USERS":
                        showAllUsers(request);
                        break;
                    case "SHOW_ORDERS":
                        showAllOrders(request);
                        break;
                    case "PROCESS_ORDER":
                        processOrder(request);
                        break;
                    case "APPROVE_ORDER":
                        approveOrder(request);
                        break;
                    case "FETCH_ORDERS":
                        fetchOrders(request);
                        break;
                    case "VIEW_ORDERS":
                        viewOrders(request);
                        break;
                    case "DISCARD_ORDER":
                        discardOrder(request);
                        break;
                    case "RESTOCK_BOOKS":
                        restockBooks(request);
                        break;
                }
                out.flush();

                requestString = in.readLine();
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void registerUser(JsonNode request) throws IOException {
        String username = request.get("username").asText();
        String email = request.get("email").asText();
        String password = request.get("password").asText();

        try {
            User user = userService.registerUser(username, email, password, Role.CLIENT);

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("id", String.valueOf(user.getId()))
                    .put("username", user.getUsername())
                    .put("email", user.getEmail())
                    .put("password", user.getPassword())
                    .put("role", user.getRole().toString());

            out.write(response.toString() + "\n");
        }
        catch(IllegalArgumentException e) {
            sendFailureResponse(e.getMessage());
        }
    }
    private void loginUser(JsonNode request) throws IOException{
        String username = request.get("username").asText();
        String password = request.get("password").asText();

        try {
            User user = userService.loginUser(username, password);

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("id", String.valueOf(user.getId()))
                    .put("username", user.getUsername())
                    .put("email", user.getEmail())
                    .put("password", user.getPassword())
                    .put("role", user.getRole().toString());

            out.write(response.toString() + "\n");

        }
        catch(UsernameNotFoundException e) {
            sendFailureResponse("User not found with this username!");
        }
        catch(NoUsersException e) {
            sendFailureResponse("No users to log into!");
        }
    }

    private void registerEmployee(JsonNode request) throws IOException {
        String adminUserName = request.get("admin").asText();
        String username = request.get("username").asText();
        String email = request.get("email").asText();
        String password = request.get("password").asText();

        User user;

        try {
            user = userService.registerUser(username, email, password, Role.EMPLOYEE);

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("username", user.getUsername())
                    .put("role", user.getRole().toString());

            System.out.printf("Admin %s registered employee %s\n", adminUserName, user.getUsername());
            out.write(response.toString() + "\n");
            out.flush();
        }
        catch(IllegalArgumentException e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private void addBook(JsonNode request) throws IOException {
        String name = request.get("name").asText();
        String author = request.get("author").asText();
        double price = request.get("price").asDouble();
        String categoriesString = request.get("categories").asText();

        List<Category> categories = getCategoriesByNames(categoriesString);

        int year = request.get("year").asInt();
        int quantity = request.get("quantity").asInt();

        String adminUserName = request.get("admin").asText();

        try {
            Book book = bookService.saveBook(name, author, price, categories, year, quantity);

            System.out.printf("Admin %s added %s\n", adminUserName, book.getName());
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("book_name", book.getName())
                    .put("author", book.getAuthor())
                    .put("quantity", book.getQuantity());

            out.write(response.toString() + "\n");
            out.flush();
        }
        catch(IllegalArgumentException e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private void removeBook(JsonNode request) throws IOException {
        try {
            String adminUserName = request.get("admin").asText();
            String bookJson = request.get("book").asText();
            Book book = objectMapper.readValue(bookJson, objectMapper.getTypeFactory().constructType(Book.class));

            bookService.deleteBook(book);

            System.out.printf("%s deleted %s by %s\n", adminUserName, book.getName(), book.getAuthor());

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success");

            out.write(response.toString() + "\n");
        }
        catch (Exception e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private void showAllBooks() throws IOException {
        try {
            List<Book> books = bookService.findAllBooks();
            String booksJson = objectMapper.writeValueAsString(books);

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("books", booksJson);

            out.write(response.toString() + "\n");
        }
        catch (NoBooksException e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private List<Category> getCategoriesByNames(String categoriesString) {
        List<Category> categories = new ArrayList<>();

        String[] categoriesNames = categoriesString.split(SEPARATOR);

        for(String categoryName : categoriesNames) {
            Category category = categoryService.getCategoryByName(categoryName.toLowerCase());

            if(category == null){
                category = categoryService.saveCategory(categoryName.toLowerCase());
            }
            categories.add(category);
        }

        return categories;
    }

    private void showAllUsers(JsonNode request) throws IOException {
        try {
            List<User> users = userService.findAllUsers();
            String usersJson = objectMapper.writeValueAsString(users);

            System.out.printf("Admin %s requested all users.%n", request.get("admin").asText());

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("users", usersJson);

            out.write(response.toString() + "\n");
        } catch (UserNotFoundException e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private void showAllOrders(JsonNode request) throws IOException {
        try {
            List<Order> orders = orderService.findAllOrders();  // Retrieve all orders
            ObjectNode response = objectMapper.createObjectNode().put("status", "success");

            String ordersJson = objectMapper.writeValueAsString(orders);
            response.put("orders", ordersJson);

            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(order);
                String orderItemsJson = objectMapper.writeValueAsString(orderItems);
                response.put("order_items_" + order.getId(), orderItemsJson);
            }

            System.out.printf("Admin %s requested all orders.%n", request.get("admin").asText());

            out.write(response + "\n");

        } catch (NoOrdersException e) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", e.getMessage());

            out.write(response.toString() + "\n");
        }
    }

    private void processOrder(JsonNode request) throws IOException {
        String username = request.get("user").asText();

        JsonNode booksWithQuantitiesNode = request.get("books_with_quantities");

        Map<Book, Integer> booksWithQuantities = new HashMap<>();
        booksWithQuantitiesNode.fields().forEachRemaining(entry -> {
            Book book = bookService.findById(UUID.fromString((entry.getKey())));
            int quantity = entry.getValue().asInt();
            booksWithQuantities.put(book, quantity);
        });

        User user = userService.loadUserByUsername(username);
        Order order = orderService.save(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Book, Integer> entry : booksWithQuantities.entrySet()) {
            OrderItem orderItem = new OrderItem(order, entry.getKey(), entry.getValue());
            orderItems.add(orderItem);
        }

        Order processedOrder = orderService.makeOrder(order, orderItems);

        if (processedOrder != null) {
            JsonNode response = objectMapper.createObjectNode().put("status", "success");
            out.write(response.toString() + "\n");
        } else {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "failure")
                    .put("message", "Failed to process the order.");
            out.write(response.toString() + "\n");
        }
    }

    private void fetchOrders(JsonNode request) throws IOException {
        String statusName = request.get("status").asText();
        Status status = StatusHelper.getStatusByName(statusName);

        try {
            List<Order> orders = orderService.findOrdersByStatus(status);
            if (orders.isEmpty()) {
                sendFailureResponse("No orders found for the status: " + statusName);
                return;
            }

            ObjectNode response = objectMapper.createObjectNode().put("status", "success");
            String ordersJson = objectMapper.writeValueAsString(orders);
            response.put("orders", ordersJson);

            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(order);
                String orderItemsJson = objectMapper.writeValueAsString(orderItems);
                response.put("order_items_" + order.getId(), orderItemsJson);
            }

            out.write(response + "\n");

        } catch (NoOrdersException e) {
            sendFailureResponse("Error fetching orders: " + e.getMessage());
        }
    }

    private void viewOrders(JsonNode request) throws IOException {
        String username = request.get("user").asText();

        try {
            User user = userService.loadUserByUsername(username);

            Status activeStatus = StatusHelper.getStatusByName("active");
            List<Order> activeOrders = orderService.findOrdersByUserAndStatus(user, activeStatus);

            Status discardedStatus = StatusHelper.getStatusByName("discarded");
            List<Order> discardedOrders = orderService.findOrdersByUserAndStatus(user, discardedStatus);

            ObjectNode response = objectMapper.createObjectNode().put("status", "success");

            String activeOrdersJson = objectMapper.writeValueAsString(activeOrders);
            response.put("active_orders", activeOrdersJson);

            for (Order order : activeOrders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(order);
                String orderItemsJson = objectMapper.writeValueAsString(orderItems);
                response.put("order_items_" + order.getId(), orderItemsJson);
            }

            String discardedOrdersJson = objectMapper.writeValueAsString(discardedOrders);
            response.put("discarded_orders", discardedOrdersJson);

            for (Order discardedOrder : discardedOrders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(discardedOrder);
                String orderItemsJson = objectMapper.writeValueAsString(orderItems);
                response.put("order_items_" + discardedOrder.getId(), orderItemsJson);
            }

            out.write(response + "\n");
        } catch (Exception e) {
            sendFailureResponse("Failed to fetch orders: " + e.getMessage());
        }
    }


    private void approveOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());

        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                sendFailureResponse("Order not found.");
                return;
            }

            Status approvedStatus = StatusHelper.getStatusByName("approved");
            orderService.changeOrderStatus(order, approvedStatus);

            for (OrderItem orderItem : orderItemService.findByOrder(order)) {
                bookService.updateBookQuantity(orderItem);
            }

            sendSuccessResponse("Order approved successfully.");

        } catch (Exception e) {
            sendFailureResponse("Failed to approve order: " + e.getMessage());
        }
    }

    private void discardOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());

        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                sendFailureResponse("Order not found.");
                return;
            }

            Status discardedStatus = StatusHelper.getStatusByName("discarded");
            orderService.changeOrderStatus(order, discardedStatus);

            sendSuccessResponse("Order discarded successfully.");

        } catch (Exception e) {
            sendFailureResponse("Failed to discard order: " + e.getMessage());
        }
    }

    private void restockBooks(JsonNode request) throws IOException {
        try {
            JsonNode booksWithQuantities = request.get("books_with_quantities");

            Map<Book, Integer> booksToRestock = new HashMap<>();
            booksWithQuantities.fieldNames().forEachRemaining(bookId -> {
                Book book = bookService.findById(UUID.fromString(bookId));
                int quantity = booksWithQuantities.get(bookId).asInt();
                booksToRestock.put(book, quantity);
            });

            // Process the restocking of each book
            for (Map.Entry<Book, Integer> entry : booksToRestock.entrySet()) {
                bookService.restockBook(entry.getKey(), entry.getValue());
            }

            sendSuccessResponse("Books restocked successfully.");

        } catch (NoBooksException e) {
            sendFailureResponse("Failed to restock books: " + e.getMessage());
        }
    }

    private void sendSuccessResponse(String message) throws IOException {
        ObjectNode response = objectMapper.createObjectNode()
                .put("status", "success")
                .put("message", message);
        out.write(response.toString() + "\n");
    }

    private void sendFailureResponse(String message) throws IOException {
        ObjectNode response = objectMapper.createObjectNode()
                .put("status", "failure")
                .put("message", message);
        out.write(response.toString() + "\n");
    }



}
