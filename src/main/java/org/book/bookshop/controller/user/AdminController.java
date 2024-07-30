package org.book.bookshop.controller.user;

import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoActiveOrdersException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.helpers.InputValidator;
import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.AdminView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController extends UserController {

    private final AdminView view;

    public AdminController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, OrderService orderService, OrderItemService orderItemService, AdminView view) {
        super(bookService, loginView, service, orderService, categoryService, orderItemService);
        this.view = view;
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input;

        try {
            input = Integer.parseInt(view.adminOptions());
        }
        catch (NumberFormatException e) {
            return -1;
        }

        switch(input) {
            case 1 -> registerEmployee();
            case 2 -> showAllUsers();
            case 3 -> addBook();
            case 4 -> removeBook();
            case 5 -> showAllBooks(true);
            case 6 -> showAllOrders();
            default -> {
                if(input != 0) { view.displayWrongOptionError(); }
            }
        }

        return input;
    }

    public void registerEmployee() {
        String[] employeeDetails = loginView.registerPrompts();

        String username = employeeDetails[0];
        String email = employeeDetails[1];
        String password = employeeDetails[2];

        try {
            service.registerUser(username, email, password, Role.EMPLOYEE);
            loginView.displayRegistrationSuccess();
        }
        catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
        }
    }

    public void addBook() {
        String[] bookDetails = view.addBook();

        String name = bookDetails[0];
        String author = bookDetails[1];
        String priceString = bookDetails[2];
        String yearString = bookDetails[3];
        String quantityString = bookDetails[4];

        double price = returnDoubleOrNegativeArgument(priceString);
        int year = returnIntegerOrNegativeArgument(yearString);
        int quantity = returnIntegerOrNegativeArgument(quantityString);

        List<Category> chosenCategories = getCategoriesByNames(bookDetails[3]);

        Book book = null;

        try {
            book = bookService.saveBook(name, author, price, chosenCategories, year, quantity);
        }
        catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
        }

        if(book == null) {
            view.displayError("Book is not successfully added!");
        }
        else {
            view.displayAddingBookSuccess();
        }
    }

    public void removeBook() {
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            String argument = view.removeBook(books);

            try {
                int index = Integer.parseInt(argument);

                bookService.deleteBook(books.get(index - 1));
                view.displayDeletingBookSuccess();
            }
            catch(RuntimeException e) {
                view.displayError("Incorrect argument!");
            }
        }
        catch (NoBooksException e) {
            view.displayError(e.getMessage());
        }
    }

    public void showAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            users = service.findAllUsers();
        }
        catch (UserNotFoundException e) {
            view.displayError(e.getMessage());
        }
        finally {
            view.showAllUsers(users);
        }
    }

    public void showAllBooks(boolean showCategories) {
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            view.showAllBooks(books, showCategories);
        }
        catch(NoBooksException e){
            view.displayError(e.getMessage());
        }
    }

    public void showAllOrders() {
        try {
            List<Order> orders = orderService.findAllOrders();
            view.showAllOrders(orders);
        }
        catch (NoActiveOrdersException e) {
            view.displayError("No orders found!");
        }

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

    private int returnIntegerOrNegativeArgument(String input) {
        return InputValidator.isInteger(input) ? Integer.parseInt(input) : -1;
    }

    private double returnDoubleOrNegativeArgument(String input) {
        return InputValidator.isDouble(input) ? Double.parseDouble(input) : -1;
    }
}
