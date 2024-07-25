package org.book.bookshop.controller.user;

import org.book.bookshop.exceptions.NoBooksException;
import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.EmployeeView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class EmployeeController extends UserController {

    private final EmployeeView view;

    public EmployeeController(BookService bookService, LoginView loginView, UserService service, CategoryService categoryService, EmployeeView employeeView, OrderItemService orderItemService, OrderService orderService) {
        super(bookService, loginView, service, orderService, categoryService, orderItemService);
        this.view = employeeView;
    }

    @Override
    public int run(User user) {
        this.user = user;

        int input = Integer.parseInt(view.employeeOptions());
        switch(input) {
            case 1 -> approveOrders();
            case 2 -> restockBooks();
            case 3 -> showAllBooks(true);
        }
        return input;
    }

    public void approveOrders() {
        view.startApprovingOrders();

        List<Order> orders = orderService.findAllOrders();
        if(orders == null) {
            view.noOrdersFound();
            return;
        }

        for(Order order : orders) {
            String answer = view.askForApprovalOfOrder(order);
            switch(answer.toLowerCase()) {
                case "y" -> approveOrder(order);
                case "n" -> discardOrder(order);
            }
        }
    }

    public void restockBooks() {
        try {
            List<Book> books = bookService.findAllBooks();
            String[] arguments = view.chooseBooksToRestock(books);

            int[] bookIndexes = parseIndexes(arguments[0]);
            int[] addedQuantities = parseQuantities(arguments[1]);

            Map<Book, Integer> booksWithAddedQuantities = prepareBooksWithAddedQuantities(books, bookIndexes, addedQuantities);

            if(!booksWithAddedQuantities.isEmpty()) {
                view.startRestocking();
                processRestocking(booksWithAddedQuantities);
                view.finishRestocking();
            }
        }
        catch (NoBooksException e) {
            view.displayError("No books to restock!");
        }
        catch(IllegalArgumentException e){
            view.displayError("Incorrect arguments!");
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

    private void approveOrder(Order order) {
        view.startApprovingOrder();
        Receipt receipt = orderService.approveOrder(order);

        if(receipt != null) {
            for(OrderItem item : receipt.getOrderItems()) {
                bookService.updateBookQuantity(item);
            }
            view.finishedApprovingOrder();
        }

    }

    private void discardOrder(Order order) {
        view.startDiscardingOrder();
        DiscardedOrder discardedOrder = orderService.discardOrder(order);

        if(discardedOrder != null) {
            view.finishDiscardingOrder();
        }
    }

    private void processRestocking(Map<Book, Integer> booksWithAddedQuantities) {
        for(Map.Entry<Book, Integer> entry : booksWithAddedQuantities.entrySet()) {
            Book bookToRestock = entry.getKey();
            int quantityToAdd = entry.getValue();

            bookService.restockBook(bookToRestock, quantityToAdd);
        }
    }

    private int[] parseIndexes(String indexArgument) {
        return Arrays.stream(indexArgument.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private int[] parseQuantities(String quantityArgument) {
        return Arrays.stream(quantityArgument.split(", "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    private Map<Book, Integer> prepareBooksWithAddedQuantities(List<Book> books, int[] bookIndexes, int[] quantities) {
        Map<Book, Integer> booksWithAddedQuantities = new HashMap<>();
        for (int i = 0; i < bookIndexes.length; i++) {
            Book book = books.get(bookIndexes[i] - 1);
            int quantity = quantities[i];

            if(quantity <= 0) {
                view.displayError("One of the quantities is below zero! It's not allowed!");
                return Collections.emptyMap();
            }
            else {
                booksWithAddedQuantities.put(book, quantity);
            }
        }
        return booksWithAddedQuantities;
    }
}
