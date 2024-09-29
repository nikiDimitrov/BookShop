package org.book.bookshop.service;

import org.book.bookshop.helpers.Result;
import org.book.bookshop.helpers.StatusHelper;
import org.book.bookshop.model.*;
import org.book.bookshop.repository.OrderItemRepository;
import org.book.bookshop.repository.OrderRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.orderItemRepository = new OrderItemRepository();
    }

    public Result<Order> findById(UUID id) {
        try {
            return orderRepository.findById(id)
                    .map(Result::success)
                    .orElse(Result.failure("Order not found!"));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching order. %s!", e.getMessage()));
        }
    }

    public Result<List<Order>> findAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            if (orders.isEmpty()) {
                return Result.failure("No orders found in database!");
            }
            return Result.success(orders);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching all orders. %s!", e.getMessage()));
        }
    }

    public Result<List<Order>> findOrdersByUserAndStatus(User user, Status status) {
        try {
            List<Order> orders = orderRepository.findByUserAndStatus(user, status);
            if (orders.isEmpty()) {
                return Result.failure("No orders found for this user and status!");
            }
            return Result.success(orders);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching orders by user and status. %s!", e.getMessage()));
        }
    }

    public Result<List<Order>> findOrdersByStatus(Status status) {
        try {
            List<Order> orders = orderRepository.findByStatus(status);
            if (orders.isEmpty()) {
                return Result.failure(String.format("No orders found for status %s!", status.getName()));
            }
            return Result.success(orders);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching orders by status. %s!", e.getMessage()));
        }
    }

    public Result<Order> save(User user) {
        try {
            Order order = new Order(user);
            Result<Status> statusResult = StatusHelper.getStatusByName("active");
            if(statusResult.isSuccess()) {
                Status status = statusResult.getValue();
                order.setStatus(status);
            }

            return Result.success(orderRepository.save(order));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while saving the order. %s!", e.getMessage()));
        }
    }

    public Result<Order> makeOrder(Order order, List<OrderItem> orderItems) {
        try {
            double totalPrice = orderItems.stream()
                    .mapToDouble(o -> o.getBook().getPrice() * o.getQuantity())
                    .sum();

            for (OrderItem orderItem : orderItems) {
                orderItemRepository.save(orderItem);
            }

            return Result.success(orderRepository.updateTotalPrice(order, totalPrice));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while processing the order. %s!", e.getMessage()));
        }
    }

    public Result<Void> changeOrderStatus(Order order, Status newStatus) {
        try {
            if (!order.getStatus().getName().equals("active")) {
                return Result.failure("Order has already been reviewed by another employee!");
            }

            order.setStatus(newStatus);
            orderRepository.updateStatus(order, newStatus);

            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while updating the order status. %s!", e.getMessage()));
        }
    }

    public Result<Void> deleteOrders(List<Order> orders) {
        try {
            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                orderItemRepository.deleteInBatch(orderItems);
            }

            orderRepository.deleteAllInBatch(orders);
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while deleting orders. %s!", e.getMessage()));
        }
    }
}
