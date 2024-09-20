package org.book.bookshop.service;

import org.book.bookshop.helpers.BookShopValidator;
import org.book.bookshop.helpers.Result;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService() {
        this.userRepository = new UserRepository();
        this.encoder = new BCryptPasswordEncoder(16);
    }

    public Result<User> registerUser(String username, String email, String password, Role role) {
        try {
            if (userRepository.findByUsername(username).isPresent() || userRepository.findByEmail(email).isPresent()) {
                return Result.failure("User with that username or email already exists!");
            }

            User user = BookShopValidator.isUserValid(username, email, password);
            if (user == null) {
                return Result.failure("Invalid user data: Username must be 3+ characters, valid email, and password must be 3+ characters.");
            }

            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);

            if (userRepository.findAll().isEmpty()) {
                user.setRole(Role.ADMIN);
            } else {
                user.setRole(role);
            }

            User savedUser = userRepository.save(user);
            return Result.success(savedUser);

        } catch (SQLException e) {
            return Result.failure(String.format("Database error while registering user. %s!", e.getMessage()));
        }
    }

    public Result<User> loginUser(String username, String password) {
        try {
            if (userRepository.findAll().isEmpty()) {
                return Result.failure("No users found in database! Can't log in.");
            }

            Result<User> userResult = loadUserByUsername(username);
            if (userResult.isFailure()) {
                return Result.failure(userResult.getError());
            }

            User user = userResult.getValue();
            if (!encoder.matches(password, user.getPassword())) {
                return Result.failure("Incorrect password for username: " + username);
            }

            return Result.success(user);

        } catch (SQLException e) {
            return Result.failure(String.format("Database error during login. %s!", e.getMessage()));
        }
    }

    public Result<List<User>> findAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return Result.failure("No users found in database!");
            }
            return Result.success(users);

        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching users. %s!", e.getMessage()));
        }
    }

    public Result<User> loadUserByUsername(String username) {
        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);
            return optionalUser.map(Result::success)
                    .orElse(Result.failure("User with username " + username + " not found!"));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while loading user. %s!", e.getMessage()));
        }
    }
}
