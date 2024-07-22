package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrdersByUser(User user) throws NoOrdersException{
        List<Order> orders = orderRepository.findByUser(user);

        if(orders == null) {
            throw new NoOrdersException("No orders found!");
        }
        else {
            return orders;
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

    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
}
