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
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            String[] arguments = view.placeOrder(books);

            try {
                int[] bookIndexes = Arrays.stream(arguments[0].split(", "))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                int[] quantities = Arrays.stream(arguments[1].split(", "))
                        .mapToInt(Integer::parseInt)
                        .toArray();

                Map<Book, Integer> booksWithQuantities = new HashMap<>();

                for(int i = 0; i < bookIndexes.length; i++) {
                    Book book = books.get(bookIndexes[i] - 1);
                    if(quantities[i] <= book.getQuantity()) {
                        booksWithQuantities.put(books.get(bookIndexes[i] - 1), quantities[i]);
                    }
                    else {
                        view.unitsTooHighError(book);
                        return;
                    }

                }

                List<Book> orderedBooks = new ArrayList<>(booksWithQuantities.keySet());

                String answer = view.confirmOrder(orderedBooks);

                if(answer.equalsIgnoreCase("y")) {
                    view.orderingBooks();

                    Order order = orderService.save(user);
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (Book orderedBook : orderedBooks) {
                        int bookQuantity = booksWithQuantities.get(orderedBook);
                        OrderItem orderItem = new OrderItem(order, orderedBook, bookQuantity);
                        orderItems.add(orderItemService.saveOrderItem(orderItem));
                    }
                    order.setOrderItems(orderItems);
                    Order result = orderService.makeOrder(order);

                    if(result == null) {
                        view.displayError("Order cannot be made!");
                    }
                    else {
                        view.displayOrderSuccessful();
                    }
                }

            }
            catch(RuntimeException e) {
                view.displayError("Incorrect argument!");
            }
        }
        catch (NoBooksException e) {
            view.displayError(e.getMessage());
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
        }
        catch (NoDiscardedOrdersException e) {
            view.displayError(e.getMessage());
        }
    }
}
