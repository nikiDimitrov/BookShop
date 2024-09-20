package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;
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
    }

    public Order() {

    }
}
