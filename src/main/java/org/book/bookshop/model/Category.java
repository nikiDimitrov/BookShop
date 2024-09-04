package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Category {
    private UUID id;

    private String name;

    public Category(String name) {
        this.name = name;
    }

    public Category() {

    }
}
