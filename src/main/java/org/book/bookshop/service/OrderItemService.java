package org.book.bookshop.service;

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

    public List<OrderItem> findByOrder(Order order) throws SQLException {
        return orderItemRepository.findByOrderId(order.getId());
    }

}
