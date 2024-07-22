package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.OrderItem;
import org.book.bookshop.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }


}
