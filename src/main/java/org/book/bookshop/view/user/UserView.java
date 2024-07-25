package org.book.bookshop.view.user;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component
public class UserView {

    public void intro(User user) {
        System.out.printf("Welcome, %s %s!\n", user.getRole(), user.getUsername());
        System.out.println("\nWhat do you want to do?");
    }

    public void awaitEnter(){
        System.out.println("Press ENTER to continue...");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
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
    }

    public void showAllOrderItems(List<OrderItem> orderItems) {
        for(int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            Book orderedBook = orderItem.getBook();

            System.out.printf("%d. %s by %s, %d, %.2f lv, %d units ordered\n", i + 1, orderedBook.getName(),
                    orderedBook.getAuthor(), orderedBook.getYear(), orderedBook.getPrice(), orderItem.getQuantity());
        }
    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void displayExitMessage(User user) {
        System.out.printf("Goodbye, %s! Exiting...\n", user.getUsername());
    }


    private String displayCategories(List<Category> categories) {
        return String.join(", ", categories
                .stream()
                .map(Category::getName)
                .toList());
    }
}
