package org.book.bookshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Username cannot be null!")
    @Min(value = 3, message = "Username should be more than 3 characters!")
    private String username;

    @Email(message = "Email should be valid!")
    private String email;

    @Min(value = 3, message = "Password should be more than 3 characters!")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
