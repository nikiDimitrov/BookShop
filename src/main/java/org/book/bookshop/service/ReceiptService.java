package org.book.bookshop.service;

import org.book.bookshop.model.Order;
import org.book.bookshop.model.Receipt;

public interface ReceiptService {
    Receipt findReceiptByOrder(Order order);

}
