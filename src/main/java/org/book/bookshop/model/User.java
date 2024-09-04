package org.book.bookshop.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID id;

    private String username;

    private String email;

    private String password;

    private Role role;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {

    }
}
