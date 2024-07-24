package org.book.bookshop.repository;

import org.book.bookshop.model.Receipt;
import org.book.bookshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    List<Receipt> findReceiptsByUser(User user);
}
