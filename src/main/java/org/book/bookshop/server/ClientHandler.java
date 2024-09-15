package org.book.bookshop.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.book.bookshop.exceptions.*;
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

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            handleClientRequests(in);

        } catch (IOException e) {
            log.error("IO error: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.info("A client has disconnected.");
        }
    }

    private void handleClientRequests(BufferedReader in) throws IOException {
        String requestString;

        while ((requestString = in.readLine()) != null) {
            JsonNode request = objectMapper.readTree(requestString);

            if (isExitCommand(request)) {
                break;
            }

            handleActionRequest(request);

            out.flush();
        }
    }

    private boolean isExitCommand(JsonNode request) {
        return request.get("action").asText().equals("EXIT");
    }

    private void handleActionRequest(JsonNode request) throws IOException {
        String action = request.get("action").asText();

        switch (action) {
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
                showAllBooks(request);
                break;
            case "REMOVE_BOOK":
                removeBook(request);
                break;
            case "SHOW_USERS":
                showAllUsers(request);
                break;
            case "SHOW_ORDERS":
                showAllOrders();
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
            case "DELETE_DISCARDED_ORDERS":
                deleteDiscardedOrders(request);
                break;
            case "RESTOCK_BOOKS":
                restockBooks(request);
                break;
            default:
                log.warn("Unknown action: {}", action);
                sendFailureResponse("Unknown action: " + action);
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

            System.out.printf("A new user has registered! Welcome %s %s!\n", user.getRole(), user.getUsername());
        }
        catch(IllegalArgumentException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private void loginUser(JsonNode request) throws IOException {
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

            System.out.printf("User %s %s has logged in!\n", user.getRole(), user.getUsername());

        } catch(UsernameNotFoundException e) {
            sendFailureResponse("User with this username not found!");
        } catch (UserNotFoundException e) {
            sendFailureResponse("User was not found!");
        } catch(NoUsersException e) {
            sendFailureResponse("No users to log into!");
        } catch(IllegalArgumentException e) {
            sendFailureResponse("Wrong password!");
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

            out.write(response.toString() + "\n");

            System.out.printf("Admin %s has registered employee %s.\n", adminUserName, user.getUsername());
        }
        catch(IllegalArgumentException e) {
            sendFailureResponse(e.getMessage());
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

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("book_name", book.getName())
                    .put("author", book.getAuthor())
                    .put("quantity", book.getQuantity());

            out.write(response.toString() + "\n");

            System.out.printf("Admin %s has added book %s by %s.\n", adminUserName, book.getName(), book.getAuthor());
        }
        catch(IllegalArgumentException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private synchronized void removeBook(JsonNode request) throws IOException {
        try {
            String adminUserName = request.get("admin").asText();
            String bookJson = request.get("book").asText();
            Book book = objectMapper.readValue(bookJson, objectMapper.getTypeFactory().constructType(Book.class));

            bookService.deleteBook(book);

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success");

            out.write(response.toString() + "\n");

            System.out.printf("User %s has deleted %s by %s.\n", adminUserName, book.getName(), book.getAuthor());
        }
        catch (IOException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private void showAllBooks(JsonNode request) throws IOException {
        try {
            List<Book> books = bookService.findAllBooks();
            String booksJson = objectMapper.writeValueAsString(books);
            String username = request.get("user").asText();

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("books", booksJson);

            System.out.printf("User %s requested all books.\n", username);
            out.write(response.toString() + "\n");
        }
        catch (NoBooksException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private void showAllUsers(JsonNode request) throws IOException {
        try {
            List<User> users = userService.findAllUsers();
            String usersJson = objectMapper.writeValueAsString(users);

            System.out.printf("Admin %s viewed all users.\n", request.get("admin").asText());

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("users", usersJson);

            out.write(response.toString() + "\n");
        } catch (UserNotFoundException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private void showAllOrders() throws IOException {
        try {
            List<Order> orders = orderService.findAllOrders();
            sendResponseWithOrders(orders);

        } catch (NoOrdersException e) {
            sendFailureResponse(e.getMessage());
        }
    }

    private void processOrder(JsonNode request) throws IOException {
        String username = request.get("user").asText();
        User user = userService.loadUserByUsername(username);

        Map<Book, Integer> booksWithQuantities = getBooksWithQuantities(request.get("books_with_quantities"));

        Order order = createAndSaveOrder(user);

        List<OrderItem> orderItems = createOrderItems(order, booksWithQuantities);

        processOrderAndRespond(order, orderItems, user);
    }

    private void fetchOrders(JsonNode request) throws IOException {
        String statusName = request.get("status").asText();
        Status status = StatusHelper.getStatusByName(statusName);
        String username = request.get("user").asText();

        try {
            List<Order> orders = orderService.findOrdersByStatus(status);
            if (orders.isEmpty()) {
                sendFailureResponse("No orders found for the status: " + statusName);
                return;
            }

            sendResponseWithOrders(orders);

            System.out.printf("Employee %s started approving orders.\n", username);

        } catch (NoOrdersException e) {
            sendFailureResponse("Error fetching orders: " + e.getMessage());
        }
    }

    private void viewOrders(JsonNode request) throws IOException {
        String username = request.get("user").asText();
        User user = userService.loadUserByUsername(username);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("status", "success");

        putToResponseActiveAndDiscardedOrders(response, user);

        out.write(response + "\n");

        System.out.printf("Client %s has viewed his approved and discarded orders.\n", user.getUsername());
    }


    private synchronized void approveOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());
        String username = request.get("user").asText();

        try {
            changeOrderStatus(orderId, "approved");
            sendSuccessResponse("Order approved successfully.");
            System.out.printf("Order %s has been approved by employee %s.\n", orderId, username);
        } catch (Exception e) {
            sendFailureResponse("Failed to approve order: " + e.getMessage());
        }
    }

    private synchronized void discardOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());
        String username = request.get("user").asText();

        try {
            changeOrderStatus(orderId, "discarded");
            sendSuccessResponse("Order discarded successfully.");
            System.out.printf("Order %s has been discarded by employee %s.\n", orderId, username);

        } catch (Exception e) {
            sendFailureResponse("Failed to discard order: " + e.getMessage());
        }
    }

    private synchronized void deleteDiscardedOrders(JsonNode request) throws IOException {
        String username = request.get("user").asText();
        String discardedOrdersJson = request.get("orders").asText();

        List<Order> discardedOrders = objectMapper.readValue(discardedOrdersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        try {
            orderService.deleteOrders(discardedOrders);

            sendSuccessResponse("Discarded orders were deleted successfully!");
            System.out.printf("Client %s's discarded orders were automatically deleted.\n", username);

        } catch (Exception e) {
            sendFailureResponse("Discarded orders couldn't be deleted!");
        }
    }

    private void restockBooks(JsonNode request) throws IOException {
        try {
            JsonNode booksWithQuantitiesNode = request.get("books_with_quantities");
            String username = request.get("user").asText();

            Map<Book, Integer> booksToRestock = getBooksWithQuantities(booksWithQuantitiesNode);
            restockEachBook(booksToRestock);

            sendSuccessResponse("Books restocked successfully.");
            System.out.printf("Employee %s restocked the inventory.\n", username);

        } catch (NoBooksException e) {
            sendFailureResponse("Failed to restock books: " + e.getMessage());
        }
    }

    private synchronized void restockEachBook(Map<Book, Integer> booksToRestock) {
        for (Map.Entry<Book, Integer> entry : booksToRestock.entrySet()) {
            bookService.restockBook(entry.getKey(), entry.getValue());
        }
    }

    private void sendResponseWithOrders(List<Order> orders) throws IOException {
        ObjectNode response = objectMapper.createObjectNode().put("status", "success");
        String ordersJson = objectMapper.writeValueAsString(orders);
        response.put("orders", ordersJson);

        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemService.findByOrder(order);
            String orderItemsJson = objectMapper.writeValueAsString(orderItems);
            response.put("order_items_" + order.getId(), orderItemsJson);
        }

        out.write(response + "\n");
    }

    private void putToResponseOrdersByStatus(String statusName, String orderType, ObjectNode response, User user) throws IOException {
        Status status = StatusHelper.getStatusByName(statusName);
        List<Order> orders = orderService.findOrdersByUserAndStatus(user, status);
        String activeOrdersJson = objectMapper.writeValueAsString(orders);

        response.put(orderType, activeOrdersJson);

        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemService.findByOrder(order);
            String orderItemsJson = objectMapper.writeValueAsString(orderItems);
            response.put("order_items_" + order.getId(), orderItemsJson);
        }
    }

    private void putToResponseActiveAndDiscardedOrders(ObjectNode response, User user) throws IOException{
        try {
            putToResponseOrdersByStatus("active", "active_orders", response, user);
        } catch (NoOrdersException e) {
            response.put("active_orders", "No active orders found!");
        }

        try {
            putToResponseOrdersByStatus("discarded", "discarded_orders", response, user);
        } catch (NoOrdersException e) {
            response.put("discarded_orders", "No discarded orders found!");
        }

    }

    private void changeOrderStatus(UUID orderId, String statusName) throws IOException{
        Order order = orderService.findById(orderId);
        if (order == null) {
            sendFailureResponse("Order not found.");
            return;
        }

        Status approvedStatus = StatusHelper.getStatusByName(statusName);

        orderService.changeOrderStatus(order, approvedStatus);

        for (OrderItem orderItem : orderItemService.findByOrder(order)) {
            bookService.updateBookQuantity(orderItem);
        }
    }

    private Map<Book, Integer> getBooksWithQuantities(JsonNode booksWithQuantitiesNode) {
        Map<Book, Integer> booksWithQuantities = new HashMap<>();
        booksWithQuantitiesNode.fields().forEachRemaining(entry -> {
            Book book = bookService.findById(UUID.fromString(entry.getKey()));
            int quantity = entry.getValue().asInt();
            booksWithQuantities.put(book, quantity);
        });
        return booksWithQuantities;
    }

    private List<OrderItem> createOrderItems(Order order, Map<Book, Integer> booksWithQuantities) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Book, Integer> entry : booksWithQuantities.entrySet()) {
            OrderItem orderItem = new OrderItem(order, entry.getKey(), entry.getValue());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void processOrderAndRespond(Order order, List<OrderItem> orderItems, User user) throws IOException {
        Order processedOrder = orderService.makeOrder(order, orderItems);

        if (processedOrder != null) {
            sendSuccessResponse("Order was made!");
            System.out.printf("User %s made an order.\n", user.getUsername());
        } else {
            sendFailureResponse("Couldn't make order!");
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

    private Order createAndSaveOrder(User user) {
        return orderService.save(user);
    }
}
