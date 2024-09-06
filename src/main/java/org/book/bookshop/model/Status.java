package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Status {
    private UUID id;

    private String name;

    public Status(String name) {
        this.name = name;
    }
}
