package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;
import org.book.bookshop.helpers.StatusHelper;

import java.sql.SQLException;
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
        try {
            status = StatusHelper.getStatusByName("active");
        }
        catch (SQLException e) {
            status = null;
        }

    }

    public Order() {

    }
}
