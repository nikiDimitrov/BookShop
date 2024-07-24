package org.book.bookshop.controller.user;

import org.book.bookshop.model.*;
import org.book.bookshop.service.*;
import org.book.bookshop.view.user.EmployeeView;
import org.book.bookshop.view.user.LoginView;
import org.springframework.stereotype.Controller;

import java.util.List;

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

    public void restockBooks() {

    }
}
