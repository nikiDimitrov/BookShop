package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.*;
import org.book.bookshop.repository.OrderItemRepository;
import org.book.bookshop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrders(User user, String status) throws NoOrdersException {
        List<Order> orders = orderRepository.findByUserAndStatus(user, status);

        if(orders.isEmpty()) {
            throw new NoOrdersException("No orders found!");
        }
        else {
            return orders;
        }
    }

    public Order save(User user) {
        return orderRepository.save(new Order(user));
    }

    public Order makeOrder(Order order, List<OrderItem> orderItems) {
        double totalPrice = orderItems
                .stream()
                .mapToDouble(o -> o.getBook().getPrice() * o.getQuantity())
                .sum();

        for(OrderItem orderItem : orderItems) {
            orderItemRepository.save(orderItem);
        }

        return orderRepository.updateTotalPrice(order, totalPrice);
    }

    public Order changeOrderStatus(Order order, String newStatus) {
        order.setStatus(newStatus);

        return orderRepository.updateStatus(order, newStatus);
    }

    public void deleteOrders(List<Order> orders) {
        for(Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            orderItemRepository.deleteInBatch(orderItems);
        }
        orderRepository.deleteAllInBatch(orders);
    }
}
