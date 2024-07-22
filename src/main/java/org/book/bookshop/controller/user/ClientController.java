package org.book.bookshop.controller.user;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.model.User;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.ClientView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

                List<Book> orderedBooks = new ArrayList<>();
                for (int bookIndex : bookIndexes) {
                    orderedBooks.add(books.get(bookIndex - 1));
                }

                String answer = view.confirmOrder(orderedBooks);

                if(answer.equalsIgnoreCase("y")) {
                    view.orderingBooks();
                    Order order = orderService.save(user);
                    List<OrderItem> orderItems = new ArrayList<>();
                    for(int i = 0; i < orderedBooks.size(); i++) {
                        OrderItem orderItem = new OrderItem(order, orderedBooks.get(i), quantities[i]);
                        orderItems.add(orderItemService.saveOrderItem(orderItem));
                    }
                    order.setOrderItems(orderItems);
                    Order result = orderService.makeOrder(order);

                    if(result == null) {
                        view.displayError("Order cannot be made!");
                    }
                    else {
                        view.displayOrderSuccessful();
                        for(OrderItem orderItem : orderItems) {
                            //this will be done after the employee finalizes the order, but this is here just for test purposes
                            bookService.updateBookQuantity(orderItem);
                        }
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
            List<Order> orders = orderService.findOrdersByUser(user);
            view.viewOrders(orders);
        }
        catch (NoOrdersException e) {
            view.displayError(e.getMessage());
        }
    }
}
