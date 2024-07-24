package org.book.bookshop.repository;

import org.book.bookshop.model.DiscardedOrder;
import org.book.bookshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscardedOrderRepository extends JpaRepository<DiscardedOrder, UUID> {
    List<DiscardedOrder> findByUser(User user);
}
