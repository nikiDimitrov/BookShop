package org.book.bookshop.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.book.bookshop.helpers.Result;
import org.book.bookshop.helpers.StatusHelper;
import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler extends Thread {
    private static final String SEPARATOR = ", *";

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private BufferedWriter out;
    private final Socket clientSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<ClientHandler> connectedClients;
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private User user;

    public ClientHandler(Socket clientSocket, List<ClientHandler> connectedClients) {
        this.clientSocket = clientSocket;
        this.connectedClients = connectedClients;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            handleClientRequests(in);

        } catch (IOException e) {
            log.error("IO error: {}", e.getMessage());
        } finally {
            removeClient();
            log.info("A client has disconnected.");
        }
    }

    private void removeClient() {
        synchronized (connectedClients) {
            connectedClients.remove(this);
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

        Result<User> result = userService.registerUser(username, email, password, Role.CLIENT);

        if(result.isSuccess()) {
            User user = getUserDetails(result);
            System.out.printf("A new user has registered! Welcome %s %s!\n", user.getRole(), user.getUsername());

            this.user = user;
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void loginUser(JsonNode request) throws IOException {
        String username = request.get("username").asText();
        String password = request.get("password").asText();

        Result<User> result = userService.loginUser(username, password);

        if(result.isSuccess()) {
            User user = getUserDetails(result);
            synchronized (connectedClients) {
                for(ClientHandler client : connectedClients) {
                    if(client.user != null &&
                            client.user.getUsername().equals(user.getUsername())) {
                        throw new IllegalStateException("User is already logged in!");
                    }
                }
            }

            System.out.printf("User %s %s has logged in!\n", user.getRole(), user.getUsername());
            this.user = user;
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void registerEmployee(JsonNode request) throws IOException {
        String adminUserName = request.get("admin").asText();
        String username = request.get("username").asText();
        String email = request.get("email").asText();
        String password = request.get("password").asText();

        Result<User> result = userService.registerUser(username, email, password, Role.EMPLOYEE);

        if(result.isSuccess()) {
            User user = result.getValue();
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("username", user.getUsername())
                    .put("role", user.getRole().toString());

            out.write(response.toString() + "\n");

            System.out.printf("Admin %s has registered employee %s.\n", adminUserName, user.getUsername());
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void addBook(JsonNode request) throws IOException {
        String name = request.get("name").asText();
        String author = request.get("author").asText();
        double price = request.get("price").asDouble();
        String categoriesString = request.get("categories").asText();

        Result<List<Category>> categoriesResult = getCategoriesByNames(categoriesString);

        if(categoriesResult.isSuccess()) {
            List<Category> categories = categoriesResult.getValue();
            int year = request.get("year").asInt();
            int quantity = request.get("quantity").asInt();

            String adminUserName = request.get("admin").asText();
            Result<Book> result = bookService.saveBook(name, author, price, categories, year, quantity);

            if(result.isSuccess()) {
                Book book = result.getValue();

                JsonNode response = objectMapper.createObjectNode()
                        .put("status", "success")
                        .put("book_name", book.getName())
                        .put("author", book.getAuthor())
                        .put("quantity", book.getQuantity());

                out.write(response.toString() + "\n");

                System.out.printf("Admin %s has added book %s by %s.\n", adminUserName, book.getName(), book.getAuthor());
            }
            else {
                checkIfConnectionErrorAndLog(result);
            }
        }
        else {
            checkIfConnectionErrorAndLog(categoriesResult);
        }
    }

    private synchronized void removeBook(JsonNode request) throws IOException {
        String adminUserName = request.get("admin").asText();
        String bookJson = request.get("book").asText();
        Book book = objectMapper.readValue(bookJson, objectMapper.getTypeFactory().constructType(Book.class));

        Result<Void> result = bookService.deleteBook(book);

        if(result.isSuccess()) {
            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success");

            out.write(response.toString() + "\n");

            System.out.printf("User %s has deleted %s by %s.\n", adminUserName, book.getName(), book.getAuthor());
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void showAllBooks(JsonNode request) throws IOException {
        Result<List<Book>> result = bookService.findAllBooks();

        if(result.isSuccess()) {
            List<Book> books = result.getValue();
            String booksJson = objectMapper.writeValueAsString(books);
            String username = request.get("user").asText();

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("books", booksJson);

            System.out.printf("User %s requested all books.\n", username);
            out.write(response.toString() + "\n");
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void showAllUsers(JsonNode request) throws IOException {
        Result<List<User>> result = userService.findAllUsers();

        if(result.isSuccess()) {
            List<User> users = result.getValue();
            String usersJson = objectMapper.writeValueAsString(users);

            System.out.printf("Admin %s viewed all users.\n", request.get("admin").asText());

            JsonNode response = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("users", usersJson);

            out.write(response.toString() + "\n");
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void showAllOrders() throws IOException {
        Result<List<Order>> result = orderService.findAllOrders();

        if(result.isSuccess()) {
            List<Order> orders = result.getValue();
            Result<Void> sendResponseResult = sendResponseWithOrders(orders);
            if(sendResponseResult.isFailure()) {
                checkIfConnectionErrorAndLog(sendResponseResult);
            }
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }

    }

    private void processOrder(JsonNode request) throws IOException {
        String username = request.get("user").asText();

        Result<User> userLoadResult = userService.loadUserByUsername(username);

        if(userLoadResult.isFailure()) {
            checkIfConnectionErrorAndLog(userLoadResult);
            return;
        }

        Result<Map<Book, Integer>> booksWithQuantitiesResult = getBooksWithQuantities(request.get("books_with_quantities"));

        if(booksWithQuantitiesResult.isFailure()) {
            checkIfConnectionErrorAndLog(booksWithQuantitiesResult);
            return;
        }

        Result<Order> orderResult = createAndSaveOrder(user);

        if(orderResult.isFailure()) {
            checkIfConnectionErrorAndLog(orderResult);
            return;
        }

        Order order = orderResult.getValue();
        Map<Book, Integer> booksWithQuantities = booksWithQuantitiesResult.getValue();

        List<OrderItem> orderItems = createOrderItems(order, booksWithQuantities);

        processOrderAndRespond(order, orderItems, user);
    }

    private void fetchOrders(JsonNode request) throws IOException {
        String statusName = request.get("status").asText();
        String username = request.get("user").asText();

        Result<Status> statusResult = StatusHelper.getStatusByName(statusName);
        if(statusResult.isFailure()) {
            checkIfConnectionErrorAndLog(statusResult);
            return;
        }

        Status status = statusResult.getValue();
        Result<List<Order>> ordersResult = orderService.findOrdersByStatus(status);

        if(ordersResult.isFailure()) {
            checkIfConnectionErrorAndLog(ordersResult);
            return;
        }

        List<Order> orders = ordersResult.getValue();
        Result<Void> sendResponseResult = sendResponseWithOrders(orders);

        if(sendResponseResult.isFailure()) {
            checkIfConnectionErrorAndLog(sendResponseResult);
            return;
        }

        System.out.printf("Employee %s started approving orders.\n", username);
    }

    private void viewOrders(JsonNode request) throws IOException {
        String username = request.get("user").asText();

        Result<User> result = userService.loadUserByUsername(username);

        if(result.isSuccess()) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("status", "success");

            putToResponseActiveAndDiscardedOrders(response, user);

            out.write(response + "\n");

            System.out.printf("Client %s has viewed his approved and discarded orders.\n", user.getUsername());
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }
    }


    private synchronized void approveOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());
        String username = request.get("user").asText();

        Result<Void> result = changeOrderStatus(orderId, "approved");
        if(result.isSuccess()) {
            sendSuccessResponse("Order approved successfully.");
            System.out.printf("Order %s has been approved by employee %s.\n", orderId, username);
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }
    }

    private synchronized void discardOrder(JsonNode request) throws IOException {
        UUID orderId = UUID.fromString(request.get("order_id").asText());
        String username = request.get("user").asText();

        Result<Void> result =  changeOrderStatus(orderId, "discarded");
        if(result.isSuccess()) {
            sendSuccessResponse("Order discarded successfully.");
            System.out.printf("Order %s has been discarded by employee %s.\n", orderId, username);
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }
    }

    private synchronized void deleteDiscardedOrders(JsonNode request) throws IOException {
        String username = request.get("user").asText();
        String discardedOrdersJson = request.get("orders").asText();

        List<Order> discardedOrders = objectMapper.readValue(discardedOrdersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        Result<Void> result = orderService.deleteOrders(discardedOrders);
        if(result.isSuccess()) {
            sendSuccessResponse("Discarded orders were deleted successfully!");
            System.out.printf("Client %s's discarded orders were automatically deleted.\n", username);
        }
        else {
            checkIfConnectionErrorAndLog(result);
        }
    }

    private void restockBooks(JsonNode request) throws IOException {
        JsonNode booksWithQuantitiesNode = request.get("books_with_quantities");
        String username = request.get("user").asText();

        Result<Map<Book, Integer>> booksToRestockResult = getBooksWithQuantities(booksWithQuantitiesNode);

        if(booksToRestockResult.isSuccess()) {
            Result<Void> restockingResult = restockEachBook(booksToRestockResult.getValue());

            if(restockingResult.isSuccess()) {
                sendSuccessResponse("Books restocked successfully.");
                System.out.printf("Employee %s restocked the inventory.\n", username);
            }
            else {
                checkIfConnectionErrorAndLog(booksToRestockResult);
            }

        }
        else {
            checkIfConnectionErrorAndLog(booksToRestockResult);
        }

    }

    private synchronized Result<Void> restockEachBook(Map<Book, Integer> booksToRestock) {
        for (Map.Entry<Book, Integer> entry : booksToRestock.entrySet()) {
            Result<Void> result = bookService.restockBook(entry.getKey(), entry.getValue());
            if(result.isFailure()) {
                return Result.failure(result.getError());
            }
        }

        return Result.success(null);
    }

    private Result<Void> sendResponseWithOrders(List<Order> orders) throws IOException {
        ObjectNode response = objectMapper.createObjectNode().put("status", "success");
        String ordersJson = objectMapper.writeValueAsString(orders);
        response.put("orders", ordersJson);

        for (Order order : orders) {
            Result<List<OrderItem>> orderItemsResult = orderItemService.findByOrder(order);
            if(orderItemsResult.isSuccess()) {
                String orderItemsJson = objectMapper.writeValueAsString(orderItemsResult.getValue());
                response.put("order_items_" + order.getId(), orderItemsJson);
            }
            else {
                return Result.failure(orderItemsResult.getError());
            }

        }

        out.write(response + "\n");

        return Result.success(null);
    }

    private Result<Void> putToResponseOrdersByStatus(String statusName, String orderType, ObjectNode response, User user) throws IOException {
        Result<Status> statusResult = StatusHelper.getStatusByName(statusName);

        if(statusResult.isFailure()) {
            return Result.failure(statusResult.getError());
        }

        Status status = statusResult.getValue();

        Result<List<Order>> ordersResult = orderService.findOrdersByUserAndStatus(user, status);

        if(ordersResult.isFailure()) {
            return Result.failure(ordersResult.getError());
        }

        List<Order> orders = ordersResult.getValue();

        String activeOrdersJson = objectMapper.writeValueAsString(orders);

        response.put(orderType, activeOrdersJson);

        for (Order order : orders) {
            Result<List<OrderItem>> orderItemsResult = orderItemService.findByOrder(order);
            if(orderItemsResult.isSuccess()) {
                List<OrderItem> orderItems = orderItemsResult.getValue();
                String orderItemsJson = objectMapper.writeValueAsString(orderItems);
                response.put("order_items_" + order.getId(), orderItemsJson);
            }
            else {
                return Result.failure(orderItemsResult.getError());
            }
        }

        return Result.success(null);
    }

    private void putToResponseActiveAndDiscardedOrders(ObjectNode response, User user) throws IOException {
        Result<Void> approveOrdersResult = putToResponseOrdersByStatus("active", "active_orders", response, user);
        if(approveOrdersResult.isFailure()) {
            response.put("active_orders", "No active orders found!");
        }

        Result<Void> discardOrdersResult = putToResponseOrdersByStatus("discarded", "discarded_orders", response, user);
        if(discardOrdersResult.isFailure()) {
            response.put("discarded_orders", "No discarded orders found!");
        }
    }

    private Result<Void> changeOrderStatus(UUID orderId, String statusName) {
        Result<Order> orderResult = orderService.findById(orderId);

        if(orderResult.isFailure()) {
            return Result.failure(orderResult.getError());
        }

        Order order = orderResult.getValue();

        Result<Status> statusResult = StatusHelper.getStatusByName(statusName);
        if(statusResult.isFailure()) {
            return Result.failure(statusResult.getError());
        }

        Status approvedStatus = statusResult.getValue();
        Result<Void> changeStatusResult = orderService.changeOrderStatus(order, approvedStatus);

        if(changeStatusResult.isFailure()) {
            return Result.failure(changeStatusResult.getError());
        }

        Result<List<OrderItem>> orderItemsResult = orderItemService.findByOrder(order);

        if(orderItemsResult.isFailure()) {
            return Result.failure(orderItemsResult.getError());
        }

        List<OrderItem> orderItems = orderItemsResult.getValue();

        for (OrderItem orderItem : orderItems) {
            Result<Void> updateQuantityResult = bookService.updateBookQuantity(orderItem);
            if(updateQuantityResult.isFailure()) {
                return Result.failure(updateQuantityResult.getError());
            }
        }

        return Result.success(null);
    }

    private Result<Map<Book, Integer>> getBooksWithQuantities(JsonNode booksWithQuantitiesNode) {
        Map<Book, Integer> booksWithQuantities = new HashMap<>();

        AtomicBoolean failure = new AtomicBoolean(false);

        booksWithQuantitiesNode.fields().forEachRemaining(entry -> {
            Result<Book> bookResult = bookService.findById(UUID.fromString(entry.getKey()));
            if (bookResult.isSuccess() && !failure.get()) {
                Book book = bookResult.getValue();
                int quantity = entry.getValue().asInt();
                booksWithQuantities.put(book, quantity);
            } else {
                failure.set(true);
            }
        });

        return failure.get() ? Result.success(booksWithQuantities) : Result.failure("Couldn't get all books");
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
       Result<Order> result = orderService.makeOrder(order, orderItems);

       if(result.isSuccess()) {
           sendSuccessResponse("Order was made!");
           System.out.printf("User %s made an order.\n", user.getUsername());
       } else {
           checkIfConnectionErrorAndLog(result);
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

    private Result<List<Category>> getCategoriesByNames(String categoriesString) {
        List<Category> categories = new ArrayList<>();
        String[] categoriesNames = categoriesString.split(SEPARATOR);

        for (String categoryName : categoriesNames) {
            Result<Category> result = categoryService.getCategoryByName(categoryName.toLowerCase());
            Category category;
            if(result.isFailure()) {
                Result<Category> savedCategoryResult = categoryService.saveCategory(categoryName);
                if(savedCategoryResult.isFailure()) {
                    return Result.failure(savedCategoryResult.getError());
                }
                else {
                    category = savedCategoryResult.getValue();
                }
            }
            else {
                category = result.getValue();
            }

            categories.add(category);
        }

        return Result.success(categories);
    }

    private User getUserDetails(Result<User> result) throws IOException {
        User user = result.getValue();
        JsonNode response = objectMapper.createObjectNode()
                .put("status", "success")
                .put("id", String.valueOf(user.getId()))
                .put("username", user.getUsername())
                .put("email", user.getEmail())
                .put("password", user.getPassword())
                .put("role", user.getRole().toString());

        out.write(response.toString() + "\n");

        return user;
    }

    private Result<Order> createAndSaveOrder(User user) {
        return orderService.save(user);
    }

    public void sendErrorMessageToClient(String message) throws IOException {
        sendFailureResponse(message);
    }

    public <T> void checkIfConnectionErrorAndLog(Result<T> result) throws IOException {
        if (result.getError().startsWith("Database error")) {
            handleDatabaseError(result);
            log.error("There is an error in the database connection: {}! Informing users...", result.getError());
        } else {
            sendFailureResponse(result.getError());
        }
    }

    public static void notifyAllClientsOfError(List<ClientHandler> connectedClients, String errorMessage) {
        for (ClientHandler client : connectedClients) {
            try {
                client.sendErrorMessageToClient(errorMessage);
            } catch (IOException e) {
                log.error("Failed to send error message to client: {}", e.getMessage());
            }
        }
    }

    private <T> void handleDatabaseError(Result<T> result) {
        log.error("Database error occurred: {}", result.getError());
        notifyAllClientsOfError(connectedClients, "A database error occurred. Please try again later.");
    }
}
