package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoBooksException;
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


    @Override
    public int run() {
        int input = Integer.parseInt(view.adminOptions());
        switch(input) {
            case 1 -> registerEmployee();
            case 2 -> showAllUsers();
            case 3 -> addBook();
            case 4 -> removeBook();
            case 5 -> showAllBooks();
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
           users = service.findAllUsers();
        }
        catch(UserNotFoundException e) {
            view.displayError(e.getMessage());
        }
        finally {
            view.showAllUsers(users);
        }
    }

    public void addBook() {
        String[] bookDetails = view.addBook();

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
        catch(NoBooksException e) {
            view.displayError(e.getMessage());
        }
    }

    public void showAllBooks() {
        List<Book> books;

        try {
            books = bookService.findAllBooks();
            view.showAllBooks(books);
        }
        catch(NoBooksException e){
            view.displayError(e.getMessage());
        }
    }


    private List<Category> getCategoriesByNames(String categoriesString) {
        List<Category> categories = new ArrayList<>();

        String[] categoriesNames = categoriesString.split(", ");

        for(String categoryName : categoriesNames) {
            Category category = categoryService.getCategoryByName(categoryName.toLowerCase());

            if(category == null){
                category = categoryService.saveCategory(categoryName.toLowerCase());
            }
            categories.add(category);
        }

        return categories;
    }
}
