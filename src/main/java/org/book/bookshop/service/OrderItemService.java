package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> findByOrder(Order order) {
        return orderItemRepository.findByOrderId(order.getId());
    }

}
