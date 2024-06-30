package org.book.bookshop.repository;

import org.book.bookshop.model.Order;
import org.book.bookshop.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    Optional<Receipt> findReceiptByOrder(Order order);
}
