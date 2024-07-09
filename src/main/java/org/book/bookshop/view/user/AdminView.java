package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AdminView extends UserView {

    @Autowired
    private final Scanner scanner;

    public String adminOptions() {
        System.out.println("1. Add an employee");
        System.out.println("2. Add a book");
        System.out.println("3. Remove a book");
        System.out.println("4. Add category");
        System.out.println("0. Exit");

        return scanner.nextLine();
    }

    public String[] addEmployee() {
        System.out.println("Username: ");
        String username = scanner.nextLine();

        System.out.println("Email: ");
        String email = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

        return new String[] { username, email, password };
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
    public void displayNoCategoryError() {
        System.out.println("No categories entered to choose from!");
    }

    public void displayBookSuccess() {
        System.out.println("Book successfully added!");
    }

    public void displayCategorySuccess() {
        System.out.println("Category successfully added!");
    }
}
