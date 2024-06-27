package org.book.bookshop.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public User(String username, String email, String password, Role role){
        this.username = username;
        this.email = email;
        setPassword(password);
        this.role = role;
    }

    public void setPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        this.password = encoder.encode(password);
    }
}
