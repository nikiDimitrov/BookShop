package org.book.bookshop.repository;

import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private final String username = "postgres";
    private final String password = System.getenv("DB_PASSWORD");

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        return getUser(username, sql);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                return Optional.of(user);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        return getUser(email, sql);
    }

    public User save(User user) {
        String sql = "INSERT INTO users (id, username, email, password, role) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();

            statement.setObject(1, id);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole().toString());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        UUID id = (UUID) resultSet.getObject("id");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        Role role = Role.valueOf(resultSet.getString("role"));

        User user = new User(username, email, password);
        user.setId(id);
        user.setRole(role);

        return user;
    }

    private Optional<User> getUser(String argument, String sql) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, argument);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                return Optional.of(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
