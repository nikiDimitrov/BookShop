package org.book.bookshop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String author;

    private double price;

    @ManyToMany
    private List<Category> categories;

    private int year;

    private int quantity;

    public Book( String name, String author, double price, List<Category> categories, int year, int quantity) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.categories = categories;
        this.year = year;
        this.quantity = quantity;
    }

    public Book() {

    }
}
