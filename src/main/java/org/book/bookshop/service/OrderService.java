package org.book.bookshop.service;

import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.*;
import org.book.bookshop.repository.OrderItemRepository;
import org.book.bookshop.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.orderItemRepository = new OrderItemRepository();
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id).stream().findFirst().orElse(null);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrdersByUserAndStatus(User user, Status status) throws NoOrdersException {
        List<Order> orders = orderRepository.findByUserAndStatus(user, status);

        if(orders.isEmpty()) {
            throw new NoOrdersException("No orders found!");
        }
        else {
            return orders;
        }
    }

    public List<Order> findOrdersByStatus(Status status) throws NoOrdersException {
        List<Order> orders = orderRepository.findByStatus(status);

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

    public void changeOrderStatus(Order order, Status newStatus) {
        order.setStatus(newStatus);

        orderRepository.updateStatus(order, newStatus);
    }

    public void deleteOrders(List<Order> orders) {
        for(Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            orderItemRepository.deleteInBatch(orderItems);
        }

        orderRepository.deleteAllInBatch(orders);
    }
}
