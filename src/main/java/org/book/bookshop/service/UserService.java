package org.book.bookshop.service;

import org.book.bookshop.helpers.BookShopValidator;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService() {
        this.userRepository = new UserRepository();
        this.encoder = new BCryptPasswordEncoder(16);
    }

    public User registerUser(String username, String email, String password, Role role) throws SQLException {
        if(userRepository.findByUsername(username).isPresent()
                || userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with that username or email already exists!");
        }

        User user = BookShopValidator.isUserValid(username, email, password);

        if(user == null) {
            throw new IllegalArgumentException("User is not valid! Username should have three characters or more, " +
                    "email should have be in correct format " +
                    "and password should have more than three characters.");
        }

        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        if(userRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
        }
        else {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public User loginUser(String username, String password) throws SQLException {
        if(userRepository.findAll().isEmpty()) {
            throw new NoSuchElementException("No users found! Can't log in!");
        }
        User user = loadUserByUsername(username);

        if(user == null) {
            throw new NoSuchElementException(String.format("User with username %s not found!", username));
        }

        if(!encoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException(String.format("Password for %s is wrong!", username));
        }

        return user;
    }

    public List<User> findAllUsers() throws SQLException {
        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            throw new NoSuchElementException("No users found in database!");
        }

        return users;
    }

    public User loadUserByUsername(String username) throws SQLException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.stream().findFirst().orElse(null);

    }
}
