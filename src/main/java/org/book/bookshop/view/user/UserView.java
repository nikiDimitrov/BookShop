package org.book.bookshop.view.user;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public class UserView {

    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public void onExit(User user) {
        System.out.printf("Goodbye, %s! Exiting...\n", user.getUsername());
    }

    public void awaitEnter() throws IOException {
        System.out.println("Press ENTER to continue...");

        try {
            int read = System.in.read(new byte[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void showAllBooks(List<Book> books, boolean showCategories) {
        for(int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if(showCategories) {
                List<Category> categories = book.getCategories();
                System.out.printf("%d. %s by %s, %d, %.2f lv., %d units - %s\n", i + 1, book.getName(), book.getAuthor(),
                        book.getYear(), book.getPrice(), book.getQuantity(), displayCategories(categories));
            }
            else {
                System.out.printf("%d. %s by %s, %d, %.2f lv., %d units\n", i + 1, book.getName(), book.getAuthor(),
                        book.getYear(), book.getPrice(), book.getQuantity());
            }
        }
        System.out.println();
    }

    private String displayCategories(List<Category> categories) {
        return String.join(", ", categories
                .stream()
                .map(Category::getName)
                .toList());
    }
}
