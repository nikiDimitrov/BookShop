package org.book.bookshop.repository;

import org.book.bookshop.model.Order;
import org.book.bookshop.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findReceiptByOrder(Order order);
}
