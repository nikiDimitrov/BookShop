package org.book.bookshop.controller.user;
import org.book.bookshop.service.BookService;
import org.book.bookshop.service.CategoryService;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.ClientView;
import org.book.bookshop.view.user.LoginView;
import org.book.bookshop.view.user.UserView;
import org.springframework.stereotype.Controller;

@Controller
public class ClientController extends UserController {

    private final ClientView view;

    public ClientController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, ClientView clientView) {
        super(bookService, loginView, service, categoryService);
        this.view = clientView;
    }

    @Override
    public int run() {
        int input = Integer.parseInt(view.clientOptions());

        switch(input) {
            case 1 -> orderBook();
            case 2 -> viewOrders();
        }

        return input;
    }

    public void orderBook() {

    }

    public void viewOrders() {

    }
}
