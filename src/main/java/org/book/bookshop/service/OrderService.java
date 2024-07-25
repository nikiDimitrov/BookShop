package org.book.bookshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoActiveOrdersException;
import org.book.bookshop.exceptions.NoDiscardedOrdersException;
import org.book.bookshop.model.*;
import org.book.bookshop.repository.OrderItemRepository;
import org.book.bookshop.repository.OrderRepository;
import org.book.bookshop.repository.ReceiptRepository;
import org.book.bookshop.repository.DiscardedOrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiscardedOrderRepository discardedOrderRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrdersByUser(User user) throws NoActiveOrdersException {
        List<Order> orders = orderRepository.findByUser(user);

        if(orders.isEmpty()) {
            throw new NoActiveOrdersException("No active orders found!");
        }
        else {
            return orders;
        }
    }

    public List<DiscardedOrder> findDiscardedOrdersByUser(User user) throws NoDiscardedOrdersException {
        List<DiscardedOrder> discardedOrders = discardedOrderRepository.findByUser(user);

        if(discardedOrders.isEmpty()) {
            throw new NoDiscardedOrdersException("No discarded orders found!");
        }
        else {
            return discardedOrders;
        }
    }

    public Order save(User user) {
        return orderRepository.save(new Order(user, new ArrayList<>()));
    }

    public Order makeOrder(Order order) {

        double totalPrice = order.getOrderItems()
                .stream()
                .mapToDouble(o -> o.getBook().getPrice() * o.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);
    }

    @Transactional
    public Receipt approveOrder(Order order) {
        Receipt receipt = new Receipt(order.getUser(), null, order.getTotalPrice());
        receipt = receiptRepository.save(receipt);

        List<OrderItem> orderItems = order.getOrderItems();

        for(OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
            orderItem.setReceipt(receipt);
            orderItemRepository.save(orderItem);
        }

        order.setOrderItems(null);
        orderRepository.save(order);

        receipt.setOrderItems(orderItems);
        receipt = receiptRepository.save(receipt);

        orderRepository.delete(order);

        return receipt;
    }

    @Transactional
    public DiscardedOrder discardOrder(Order order) {
        DiscardedOrder discardedOrder = new DiscardedOrder(order.getUser(), null, order.getTotalPrice());
        discardedOrder = discardedOrderRepository.save(discardedOrder);

        List<OrderItem> orderItems = order.getOrderItems();

        for(OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
            orderItem.setDiscardedOrder(discardedOrder);
            orderItemRepository.save(orderItem);
        }

        order.setOrderItems(null);
        orderRepository.save(order);

        discardedOrder.setOrderItems(orderItems);
        discardedOrder = discardedOrderRepository.save(discardedOrder);

        orderRepository.delete(order);

        return discardedOrder;
    }

    @Transactional
    public void deleteDiscardedOrders(List<DiscardedOrder> discardedOrders) {
        for(DiscardedOrder discardedOrder : discardedOrders) {
            List<OrderItem> orderItems = discardedOrder.getOrderItems();

            for(OrderItem orderItem : orderItems) {
                orderItem.setDiscardedOrder(null);
                orderItemRepository.save(orderItem);
                orderItemRepository.delete(orderItem);
            }

            discardedOrderRepository.delete(discardedOrder);
        }
    }
}
