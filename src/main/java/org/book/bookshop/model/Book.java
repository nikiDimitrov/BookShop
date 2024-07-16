package org.book.bookshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Category> categories;

    private int year;

    public Book(String name, String author, double price, List<Category> categories, int year) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.categories = categories;
        this.year = year;
    }
}
