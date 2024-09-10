package org.book.bookshop.controller.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.*;
import org.book.bookshop.view.user.EmployeeView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class EmployeeController extends UserController {

    private final EmployeeView view;

    public EmployeeController(BufferedWriter out, BufferedReader in) {
        super(out, in);
        this.view = new EmployeeView();
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input;

        try {
            input = Integer.parseInt(view.employeeOptions());
        }
        catch (NumberFormatException e) {
            return -1;
        }

        switch(input) {
            case 1 -> approveOrders();
            case 2 -> restockBooks();
            case 3 -> showAllBooks(true);
            default -> {
                if(input != 0) { view.displayWrongOptionError(); }
            }
        }
        return input;
    }

    public void approveOrders() {
        try {
            JsonNode request = objectMapper.createObjectNode()
                    .put("action", "FETCH_ORDERS")
                    .put("status", "active")
                    .put("user", user.getUsername());

            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if (status.equals("success")) {
                String ordersJson = response.get("orders").toString();
                List<Order> orders = objectMapper.readValue(ordersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

                if (orders.isEmpty()) {
                    view.noOrdersFound();
                    return;
                }

                for (Order order : orders) {
                    String orderItemsJson = response.get("order_items_" + order.getId()).toString();
                    List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class));

                    String answer = view.askForApprovalOfOrder(order, orderItems);
                    if ("y".equalsIgnoreCase(answer)) {
                        approveOrder(order);
                    } else {
                        discardOrder(order);
                    }
                }
            } else {
                view.displayError(response.get("message").asText());
            }
        } catch (IOException e) {
            view.displayError("Failed to communicate with the server.");
        }
    }


    public void restockBooks() {
        try {
            List<Book> books = getAllBooks();

            String[] arguments = view.chooseBooksToRestock(books);
            int[] bookIndexes = parseIndexes(arguments[0]);
            int[] addedQuantities = parseQuantities(arguments[1]);

            Map<Book, Integer> booksWithAddedQuantities = prepareBooksWithAddedQuantities(books, bookIndexes, addedQuantities);

            if (!booksWithAddedQuantities.isEmpty()) {
                view.startRestocking();
                processRestocking(booksWithAddedQuantities);

                view.finishRestocking();
            }
        } catch (NoBooksException e) {
            view.displayError("No books to restock!");
        } catch (IOException e) {
            view.displayError("Failed to communicate with the server.");
        } catch (IllegalArgumentException e) {
            view.displayError("Incorrect arguments!");
        }
    }

    private void processRestocking(Map<Book, Integer> booksWithAddedQuantities) throws IOException {
        ObjectNode restockRequest = objectMapper.createObjectNode()
                .put("action", "RESTOCK_BOOKS")
                .put("user", user.getUsername());

        ObjectNode booksNode = objectMapper.createObjectNode();
        for (Map.Entry<Book, Integer> entry : booksWithAddedQuantities.entrySet()) {
            booksNode.put(String.valueOf(entry.getKey().getId()), entry.getValue());
        }
        restockRequest.set("books_with_quantities", booksNode);

        out.write(restockRequest + "\n");
        out.flush();

        JsonNode response = objectMapper.readTree(in.readLine());
        String status = response.get("status").asText();

        if (status.equals("success")) {
            view.finishRestocking();
        } else {
            view.displayError(response.get("message").asText());
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
            view.displayError("Failed to communicate with the server.");
        }
    }


    public void approveOrder(Order order) {
        try {
            // Prepare the JSON request to approve the order
            JsonNode approveRequest = objectMapper.createObjectNode()
                    .put("action", "APPROVE_ORDER")
                    .put("order_id", String.valueOf(order.getId()))
                    .put("user", user.getUsername());

            // Send the request to the server
            out.write(approveRequest.toString() + "\n");
            out.flush();

            // Read the response from the server
            JsonNode response = objectMapper.readTree(in.readLine());

            // Check if the approval was successful
            if (response.get("status").asText().equals("success")) {
                view.finishedApprovingOrder();
            } else {
                view.displayError(response.get("message").asText());
            }
        } catch (IOException e) {
            view.displayError("Failed to communicate with the server.");
        }
    }


    private void discardOrder(Order order) {
        try {
            // Prepare the discard request
            JsonNode discardRequest = objectMapper.createObjectNode()
                    .put("action", "DISCARD_ORDER")
                    .put("order_id", String.valueOf(order.getId()))
                    .put("user", user.getUsername());

            out.write(discardRequest.toString() + "\n");
            out.flush();

            // Process the server response
            JsonNode response = objectMapper.readTree(in.readLine());
            if (response.get("status").asText().equals("success")) {
                view.finishDiscardingOrder();
            } else {
                view.displayError(response.get("message").asText());
            }
        } catch (IOException e) {
            view.displayError("Failed to communicate with the server.");
        }
    }



    private int[] parseIndexes(String indexArgument) {
        return Arrays.stream(indexArgument.split(SEPARATOR))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int[] parseQuantities(String quantityArgument) {
        return Arrays.stream(quantityArgument.split(SEPARATOR))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private Map<Book, Integer> prepareBooksWithAddedQuantities(List<Book> books, int[] bookIndexes, int[] quantities) {
        Map<Book, Integer> booksWithAddedQuantities = new HashMap<>();

        if(bookIndexes.length != quantities.length) {
            view.displayUnequalNumberOfArgumentsError();
            return Collections.emptyMap();
        }

        for (int i = 0; i < bookIndexes.length; i++) {
            Book book;
            try {
                book = books.get(bookIndexes[i] - 1);
            }
            catch (IndexOutOfBoundsException e) {
                view.displayWrongIndexError();
                return Collections.emptyMap();
            }

            int quantity = quantities[i];

            if(quantity <= 0) {
                view.displayNegativeQuantityError();
                return Collections.emptyMap();
            }
            else {
                booksWithAddedQuantities.put(book, quantity);
            }
        }
        return booksWithAddedQuantities;
    }
}
