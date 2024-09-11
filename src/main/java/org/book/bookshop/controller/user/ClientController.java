package org.book.bookshop.controller.user;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.*;
import org.book.bookshop.view.user.ClientView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;


public class ClientController extends UserController {

    private final ClientView view;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientController(BufferedWriter out, BufferedReader in) {
        super(out, in);
        this.view = new ClientView();
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input;

        try {
            input = Integer.parseInt(view.clientOptions());
        }
        catch (NumberFormatException e) {
            return -1;
        }

        switch(input) {
            case 1 -> orderBooks();
            case 2 -> viewOrders();
            default -> {
                if(input != 0) { view.displayWrongOptionError(); }
            }
        }

        return input;
    }

    public void orderBooks() {
        try {
            List<Book> books = getAllBooks();

            String[] arguments = view.placeOrder(books);

            int[] bookIndexes = parseIndexes(arguments[0]);
            int[] quantities = parseQuantities(arguments[1]);

            Map<Book, Integer> booksWithQuantities = prepareOrder(books, bookIndexes, quantities);

            if (!booksWithQuantities.isEmpty() && confirmOrder(booksWithQuantities)) {
                processOrder(booksWithQuantities);
            }

        } catch (NoBooksException e) {
            view.displayError(e.getMessage());
        } catch (IOException e) {
            view.displayError("Failed to communicate with the server.");
        } catch (RuntimeException e) {
            view.displayError("Incorrect argument!");
        }
    }

    private void processOrder(Map<Book, Integer> booksWithQuantities) throws IOException {
        view.orderingBooks();

        ObjectNode orderRequest = objectMapper.createObjectNode()
                .put("action", "PROCESS_ORDER")
                .put("user", user.getUsername());

        ObjectNode booksNode = objectMapper.createObjectNode();
        for (Map.Entry<Book, Integer> entry : booksWithQuantities.entrySet()) {
            booksNode.put(String.valueOf(entry.getKey().getId()), entry.getValue());
        }
        orderRequest.set("books_with_quantities", booksNode);

        out.write(orderRequest + "\n");
        out.flush();

        JsonNode response = objectMapper.readTree(in.readLine());
        String status = response.get("status").asText();

        if (status.equals("success")) {
            view.displayOrderSuccessful();
        } else {
            view.displayError(response.get("message").asText());
        }
    }

    public void viewOrders() {
        try {
            Map<Order, List<OrderItem>> ordersWithItems = new HashMap<>();

            JsonNode request = objectMapper.createObjectNode()
                    .put("action", "VIEW_ORDERS")
                    .put("user", user.getUsername());

            out.write(request.toString() + "\n");
            out.flush();

            JsonNode response = objectMapper.readTree(in.readLine());
            String status = response.get("status").asText();

            if (status.equals("success")) {
                String activeOrdersJson = response.get("active_orders").asText();
                if (!activeOrdersJson.equals("No active orders found!")) {
                    List<Order> activeOrders = objectMapper.readValue(activeOrdersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

                    for (Order order : activeOrders) {
                        String orderItemsJson = response.get("order_items_" + order.getId()).asText();
                        List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class));
                        ordersWithItems.put(order, orderItems);
                    }
                    view.viewActiveOrders(ordersWithItems);
                } else {
                    view.displayError(activeOrdersJson);
                }

                String discardedOrdersJson = response.get("discarded_orders").asText();
                if (!discardedOrdersJson.equals("No discarded orders found!")) {
                    List<Order> discardedOrders = objectMapper.readValue(discardedOrdersJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

                    ordersWithItems.clear();
                    for (Order discardedOrder : discardedOrders) {
                        String orderItemsJson = response.get("order_items_" + discardedOrder.getId()).asText();
                        List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class));
                        ordersWithItems.put(discardedOrder, orderItems);
                    }

                    view.viewDiscardedOrders(ordersWithItems);
                    deleteDiscardedOrders(discardedOrders);
                } else {
                    view.displayError(discardedOrdersJson);
                }
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

    private Map<Book, Integer> prepareOrder(List<Book> books, int[] bookIndexes, int[] quantities) {
        Map<Book, Integer> booksWithQuantities = new HashMap<>();

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

            if (quantity <= book.getQuantity()) {
                booksWithQuantities.put(book, quantity);
            } else {
                view.unitsTooHighError(book);
                return Collections.emptyMap();
            }
        }

        return booksWithQuantities;
    }

    private boolean confirmOrder(Map<Book, Integer> booksWithQuantities) {
        List<OrderItem> orderItems = convertMapToOrderItems(booksWithQuantities);
        String answer = view.confirmOrder(orderItems);

        return answer.equalsIgnoreCase("y");
    }


    private List<OrderItem> convertMapToOrderItems(Map<Book, Integer> books) {
        List<OrderItem> orderItems = new ArrayList<>();

        for(Map.Entry<Book, Integer> entry : books.entrySet()) {
            OrderItem orderItem = new OrderItem(null, entry.getKey(), entry.getValue());
            orderItems.add(orderItem);
        }

        return orderItems.stream()
                .sorted(Comparator.comparing(o -> o.getBook().getName()))
                .toList();
    }

    private void deleteDiscardedOrders(List<Order> discardedOrders) throws IOException {
        String discardedOrdersJson = objectMapper.writeValueAsString(discardedOrders);

        JsonNode deleteRequest = objectMapper.createObjectNode()
                .put("action", "DELETE_DISCARDED_ORDERS")
                .put("user", user.getUsername())
                .put("orders", discardedOrdersJson);

        out.write(deleteRequest.toString() + "\n");
        out.flush();

        JsonNode response = objectMapper.readTree(in.readLine());
        if (response.get("status").asText().equals("success")) {
            view.displaySuccessfullyDeletedDiscardedOrders();
        } else {
            view.displayError("Failed to delete discarded orders: " + response.get("message").asText());
        }
    }
}
