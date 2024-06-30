package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.Receipt;
import org.book.bookshop.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public Receipt findReceiptByOrder(Order order) {
        Optional<Receipt> receipt = receiptRepository.findReceiptByOrder(order);
        return receipt.stream().findFirst().orElse(null);
    }
}
