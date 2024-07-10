package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.exceptions.NoCategoriesException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.BookService;
import org.book.bookshop.service.CategoryService;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.user.AdminView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController extends UserController {

    @Autowired
    private final UserService service;
    private final CategoryService categoryService;
    private final BookService bookService;

    private final AdminView view;
    private final LoginView loginView;
    @Autowired
    private UserService userService;


    @Override
    public int run() {
        int input = Integer.parseInt(view.adminOptions());
        switch(input) {
            case 1 -> registerEmployee();
            case 2 -> showAllUsers();
            case 3 -> addBook();
            case 5 -> showAllBooks();
            case 6 -> addCategory();
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

    public void showAllUsers() {
        List<User> users = new ArrayList<>();

        try {
           users = userService.findAllUsers();
        }
        catch(UserNotFoundException e) {
            view.displayError(e.getMessage());
        }
        finally {
            view.showAllUsers(users);
        }
    }

    public void addBook() {
        List<Category> categories = new ArrayList<>();

        try {
            categories = categoryService.getAllCategories();
        }
        catch (NoCategoriesException e) {
            view.displayError(e.getMessage());
        }
        finally {
            String[] bookDetails = view.addBook(categories);

            String name = bookDetails[0];
            String author = bookDetails[1];
            double price = Double.parseDouble(bookDetails[2]);
            List<Category> chosenCategories = getCategoriesByNames(bookDetails[3]);
            int year = Integer.parseInt(bookDetails[4]);

            Book book = bookService.saveBook(name, author, price, chosenCategories, year);

            if(book == null) {
                //needs to be refactored, temporary code
                view.displayError("Book is not successfully added!");
            }
            else {
                view.displayBookSuccess();
            }
        }
    }

    public void showAllBooks() {
        List<Book> books = new ArrayList<>();

        try {
            books = bookService.findAllBooks();
        }
        catch(NoBooksException e){
            view.displayError(e.getMessage());
        }
        finally {
            view.showAllBooks(books);
        }
    }

    public void addCategory() {
        List<String> categoryNames = view.addCategories();

        for(String categoryName : categoryNames) {
            Category category = categoryService.saveCategory(categoryName);

            if(category == null) {
                //needs refactoring too
                throw new RuntimeException("Category wasn't added successfully!");
            }
            else {
                view.displayCategorySuccess();
            }
        }
    }


    private List<Category> getCategoriesByNames(String categoriesString) {
        List<Category> categories = new ArrayList<>();

        String[] categoriesNames = categoriesString.split(", ");

        for(String categoryName : categoriesNames) {
            Category category = categoryService.getCategoryByName(categoryName);
            categories.add(category);
        }

        return categories;
    }
}
