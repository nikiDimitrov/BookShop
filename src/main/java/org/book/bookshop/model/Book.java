package org.book.bookshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @Size(min = 3, message = "Book name should be more than 3 characters!")
    private String name;

    @Size(min = 3, message = "Author's name should be more than 3 characters!")
    private String author;

    @Positive(message = "Price should be more than zero!")
    private double price;

    @ManyToMany
    private List<Category> categories;

    @Positive(message = "Year should be a positive number!")
    private int year;

    @Positive(message = "Quantity should be more than zero!")
    private int quantity;

    public Book( String name, String author, double price, List<Category> categories, int year, int quantity) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.categories = categories;
        this.year = year;
        this.quantity = quantity;
    }
}
