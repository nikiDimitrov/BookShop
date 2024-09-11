package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;
import org.book.bookshop.helpers.StatusHelper;

import java.util.UUID;

@Getter
@Setter
public class Order {

    private UUID id;

    private User user;

    private double totalPrice;

    private Status status;

    public Order(User user) {
        this.user = user;
        status = StatusHelper.getStatusByName("active");
    }

    public Order() {

    }
}
