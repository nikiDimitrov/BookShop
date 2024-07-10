package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class AdminView extends UserView {


    public AdminView(Scanner scanner) {
        super(scanner);
    }

    public String adminOptions() {
        System.out.println("1. Add an employee");
        System.out.println("2. Show all users");
        System.out.println("3. Add a book");
        System.out.println("4. Remove a book");
        System.out.println("5. Show all books");
        System.out.println("6. Add category");
        System.out.println("0. Exit");

        return scanner.nextLine();
    }

    public void showAllUsers(List<User> users){
        System.out.println("List of all users:\n");

        for(int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.printf("%d. %s %s %s\n", i + 1, user.getUsername(),
                    user.getEmail(), user.getRole());
        }

        System.out.println();
    }
    public String[] addBook(List<Category> categories) {
        System.out.println("Name of book: ");
        String name = scanner.nextLine();

        System.out.println("Author: ");
        String author = scanner.nextLine();

        System.out.println("Price: ");
        String price = scanner.nextLine();

        System.out.println("Categories: ");

        for(int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i).getName());
        }
        System.out.println("Type categories: ");

        String categoriesString = scanner.nextLine();

        System.out.println("Year: ");
        String year = scanner.nextLine();

        return new String[] { name, author, price, categoriesString, year };
    }

    public void showAllBooks(List<Book> books) {
        System.out.println("All books present: \n");

        for(int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.printf("%d. %s by %s, %d\n", i + 1, book.getName(), book.getAuthor(),
                    book.getYear());
        }

        System.out.println();
    }

    public List<String> addCategories() {
        List<String> categories = new ArrayList<>();

        System.out.println("Type END when you want to stop entering...");
        System.out.println("Name of category: ");
        String categoryName = scanner.nextLine();

        while(!categoryName.equalsIgnoreCase("end")) {
            categories.add(categoryName);
            System.out.println("Name of category: ");
            categoryName = scanner.nextLine();
        }

        return categories;
    }

    public void displayBookSuccess() {
        System.out.println("Book successfully added!");
    }

    public void displayCategorySuccess() {
        System.out.println("Category successfully added!");
    }

}
