package org.book.bookshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "discarded_orders")
public class DiscardedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "discardedOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    private double totalPrice;

    public DiscardedOrder(User user, List<OrderItem> orderItems, double totalPrice) {
        this.user = user;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

}
