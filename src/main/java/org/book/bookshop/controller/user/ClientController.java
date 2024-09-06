package org.book.bookshop.controller.user;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.helpers.StatusHelper;
import org.book.bookshop.model.*;
import org.book.bookshop.view.user.ClientView;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class ClientController extends UserController {

    private final ClientView view;

    public ClientController() {
        super();
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
        } catch (RuntimeException e) {
            view.displayError("Incorrect argument!");
        }
    }

    public void viewOrders() {
        try {
            view.startingDisplayActiveOrders();

            Status active = StatusHelper.getStatusByName("active");

            List<Order> orders = orderService.findOrdersByUserAndStatus(user, active);

            Map<Order, List<OrderItem>> ordersWithItems = new HashMap<>();

            for(Order order : orders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(order);

                ordersWithItems.put(order, orderItems);
            }

            view.viewActiveOrders(ordersWithItems);
        }
        catch (NoOrdersException e) {
            view.displayError(e.getMessage());
        }

        try {
            Map<Order, List<OrderItem>> discardedOrdersWithItems = new HashMap<>();

            view.startingDisplayDiscardedOrders();

            Status discardedStatus = StatusHelper.getStatusByName("discarded");

            List<Order> discardedOrders = orderService.findOrdersByUserAndStatus(user, discardedStatus);

            for(Order discardedOrder : discardedOrders) {
                List<OrderItem> orderItems = orderItemService.findByOrder(discardedOrder);
                discardedOrdersWithItems.put(discardedOrder, orderItems);
            }

            view.viewDiscardedOrders(discardedOrdersWithItems);

            if(!discardedOrdersWithItems.isEmpty()) {
                CompletableFuture.runAsync(() -> orderService.deleteOrders(discardedOrders));

                view.displaySuccessfullyDeletedDiscardedOrders();
            }

        }
        catch (NoOrdersException e) {
            view.displayError(e.getMessage());
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

    private void processOrder(Map<Book, Integer> booksWithQuantities) {
        List<OrderItem> orderItems = new ArrayList<>();
        view.orderingBooks();

        Order order = orderService.save(user);
        
        for (Map.Entry<Book, Integer> entry : booksWithQuantities.entrySet()) {
            OrderItem orderItem = new OrderItem(order, entry.getKey(), entry.getValue());
            orderItems.add(orderItem);
        }

        Order result = orderService.makeOrder(order, orderItems);

        if (result == null) {
            view.displayError("Order cannot be made!");
        } else {
            view.displayOrderSuccessful();
        }
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
}
