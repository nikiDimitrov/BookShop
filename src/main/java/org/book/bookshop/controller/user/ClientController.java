package org.book.bookshop.controller.user;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import org.book.bookshop.service.BookService;
import org.book.bookshop.service.CategoryService;
import org.book.bookshop.service.OrderService;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.ClientView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClientController extends UserController {

    private final ClientView view;

    public ClientController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, OrderService orderService, ClientView clientView) {
        super(bookService, loginView, service, orderService, categoryService);
        this.view = clientView;
    }

    @Override
    public int run(User user) {
        this.user = user;
        int input = Integer.parseInt(view.clientOptions());

        switch(input) {
            case 1 -> orderBook();
            case 2 -> viewOrders();
        }

        return input;
    }

    public void orderBook() {
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            String argument = view.placeOrder(books);

            try {
                int index = Integer.parseInt(argument);

                Book book = books.get(index - 1);
                String answer = view.confirmOrder(book);

                if(answer.equalsIgnoreCase("y")) {
                    view.orderingBook(book);
                    Order order = orderService.makeOrder(user, book);

                    if(order == null) {
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
            List<Order> orders = orderService.findOrdersByUser(user);
            view.viewOrders(orders);
        }
        catch (NoOrdersException e) {
            view.displayError(e.getMessage());
        }
    }
}
