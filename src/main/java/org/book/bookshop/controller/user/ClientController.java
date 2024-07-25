package org.book.bookshop.controller.user;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoActiveOrdersException;
import org.book.bookshop.exceptions.NoDiscardedOrdersException;
import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.ClientView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class ClientController extends UserController {

    private final ClientView view;
    private final BookService bookService;

    public ClientController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, OrderService orderService, OrderItemService orderItemService, ClientView clientView) {
        super(bookService, loginView, service, orderService, categoryService, orderItemService);
        this.view = clientView;
        this.bookService = bookService;
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input = Integer.parseInt(view.clientOptions());

        switch(input) {
            case 1 -> orderBooks();
            case 2 -> viewOrders();
        }

        return input;
    }

    public void orderBooks() {
        try {
            List<Book> books = bookService.findAllBooks();
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
            List<Order> orders = orderService.findOrdersByUser(user);
            view.viewOrders(orders);
        }
        catch (NoActiveOrdersException e) {
            view.displayError(e.getMessage());
        }

        try {
            view.startingDisplayDiscardedOrders();
            List<DiscardedOrder> discardedOrders = orderService.findDiscardedOrdersByUser(user);
            view.viewDiscardedOrders(discardedOrders);

            if(!discardedOrders.isEmpty()) {
                orderService.deleteDiscardedOrders(discardedOrders);
                view.displaySuccessfullyDeletedDiscardedOrders();
            }

        }
        catch (NoDiscardedOrdersException e) {
            view.displayError(e.getMessage());
        }
    }


    private int[] parseIndexes(String indexArgument) {
        return Arrays.stream(indexArgument.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int[] parseQuantities(String quantityArgument) {
        return Arrays.stream(quantityArgument.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private Map<Book, Integer> prepareOrder(List<Book> books, int[] bookIndexes, int[] quantities) {
        Map<Book, Integer> booksWithQuantities = new HashMap<>();
        for (int i = 0; i < bookIndexes.length; i++) {
            Book book = books.get(bookIndexes[i] - 1);
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
        view.orderingBooks();

        Order order = orderService.save(user);
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map.Entry<Book, Integer> entry : booksWithQuantities.entrySet()) {
            OrderItem orderItem = new OrderItem(order, entry.getKey(), entry.getValue());
            orderItems.add(orderItemService.saveOrderItem(orderItem));
        }
        order.setOrderItems(orderItems);
        Order result = orderService.makeOrder(order);

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

        return orderItems;
    }
}
