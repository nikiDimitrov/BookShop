package org.book.bookshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Book book;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;

    public OrderItem(Order order, Book book, int quantity) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
    }

    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
}

