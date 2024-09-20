package org.book.bookshop.service;

import org.book.bookshop.helpers.Result;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.OrderItemRepository;

import java.sql.SQLException;
import java.util.List;

public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService() {
        this.orderItemRepository = new OrderItemRepository();
    }

    public Result<List<OrderItem>> findByOrder(Order order) {
        try {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            if(orderItems.isEmpty()) {
                return Result.failure("No order items found for this order id!");
            }

            return Result.success(orderItems);
        }
        catch(SQLException e) {
            return Result.failure(String.format("Database error while finding order items. %s!", e.getMessage()));
        }
    }

}
