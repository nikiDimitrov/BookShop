package org.book.bookshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z ]{3,}+$", message = "Category should contain only letters and should have length more than three!")
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
