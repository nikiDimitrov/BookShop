package org.book.bookshop.service.impl;

import org.book.bookshop.model.Order;
import org.book.bookshop.model.Receipt;
import org.book.bookshop.repository.ReceiptRepository;
import org.book.bookshop.service.ReceiptService;

import java.util.Optional;

public class ReceiptServiceImpl implements ReceiptService {
    private ReceiptRepository receiptRepository;

    public ReceiptServiceImpl(ReceiptRepository receiptRepository){
        this.receiptRepository = receiptRepository;
    }

    @Override
    public Receipt findReceiptByOrder(Order order) {
        Optional<Receipt> receipt = receiptRepository.findReceiptByOrder(order);
        return receipt.stream().findFirst().orElse(null);
    }
}
