package org.book.bookshop.repository;

import org.book.bookshop.model.Order;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepository {
    private String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private String username = "postgres";
    private String password = System.getenv("DB_PASSWORD");
    private String sqlSelect ="SELECT o.id AS order_id, o.user_id AS order_user_id, o.total_price AS order_total_price, o.status AS order_status, " +
            "u.id AS user_id, u.username AS user_username, u.email AS user_email, u.password AS user_password, u.role AS user_role FROM orders AS o" +
            " JOIN users AS u ON o.user_id = u.id ";

    public List<Order> findByUserAndStatus(User user, String status) {
        List<Order> orders = new ArrayList<>();

        String sql = sqlSelect +
                "WHERE o.user_id = ? AND o.status = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, user.getId());
            statement.setString(2, status);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Optional<Order> findById(UUID id) {
        String sql = sqlSelect +
                "WHERE o.id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);

                return Optional.of(order);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();

        String sql = sqlSelect +
                "WHERE o.status = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();

        String sql = sqlSelect;

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public Order updateTotalPrice(Order order, double totalPrice) {
        String sql = "UPDATE orders SET total_price = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, totalPrice);
            statement.setObject(2, order.getId());

            statement.executeUpdate();

            return findById(order.getId()).stream().findFirst().orElse(null);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Order updateStatus(Order order, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setObject(2, order.getId());

            statement.executeUpdate();

            return findById(order.getId()).stream().findFirst().orElse(null);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public Order save(Order order) {
        String sql = "INSERT INTO orders (id, status, total_price, user_id) VALUES (?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();

            statement.setObject(1, id);
            statement.setString(2, order.getStatus());
            statement.setDouble(3, order.getTotalPrice());
            statement.setObject(4, order.getUser().getId());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Order order) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, order.getId());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllInBatch(List<Order> orders) {
        for(Order order : orders) {
            delete(order);
        }
    }

    private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
        UUID orderId = (UUID) resultSet.getObject("order_id");
        UUID userId = (UUID) resultSet.getObject("user_id");
        double totalPrice = resultSet.getDouble("order_total_price");
        String status = resultSet.getString("order_status");

        String username = resultSet.getString("user_username");
        String email = resultSet.getString("user_email");
        String password = resultSet.getString("user_password");
        Role role = Role.valueOf(resultSet.getString("user_role"));

        User user = new User(username, email, password);
        user.setId(userId);
        user.setRole(role);

        Order order = new Order(user);

        order.setId(orderId);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);

        return order;
    }
}
