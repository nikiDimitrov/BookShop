package org.book.bookshop.service;

import org.book.bookshop.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAllOrders();
    List<Order> findOrdersByUsername(String username);
    Order saveOrder(Order order);
    void deleteOrder(Order order);
}
