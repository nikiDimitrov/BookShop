package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.NoOrdersException;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

    public Order makeOrder(User user, Book... booksElements) {
        List<Book> books = Arrays.stream(booksElements).toList();

        Order order = new Order(user, books);
        double totalPrice = order.getBooks()
                .stream()
                .mapToDouble(Book::getPrice)
                .sum();
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);
    }

    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
}
