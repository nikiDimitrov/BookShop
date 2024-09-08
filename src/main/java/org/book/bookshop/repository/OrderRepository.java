package org.book.bookshop.repository;

import org.book.bookshop.helpers.DatabaseConnection;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.Status;
import org.book.bookshop.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepository {
    private final String SQLSELECT ="SELECT o.id AS order_id, o.user_id AS order_user_id, o.total_price AS order_total_price, " +
            "s.id AS status_id, s.name AS status_name, " +
            "u.id AS user_id, u.username AS user_username, u.email AS user_email, u.password AS user_password, u.role AS user_role FROM orders AS o" +
            " JOIN users AS u ON o.user_id = u.id" +
            " JOIN statuses as s ON o.status_id = s.id ";

    public List<Order> findByUserAndStatus(User user, Status status) {
        List<Order> orders = new ArrayList<>();

        String sql = SQLSELECT +
                "WHERE o.user_id = ? AND o.status_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, user.getId());
            statement.setObject(2, status.getId());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return orders;
    }

    public Optional<Order> findById(UUID id) {
        String sql = SQLSELECT +
                "WHERE o.id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);

                return Optional.of(order);
            }
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return Optional.empty();
    }

    public List<Order> findByStatus(Status status) {
        List<Order> orders = new ArrayList<>();

        String sql = SQLSELECT +
                "WHERE o.status_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, status.getId());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return orders;
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();


        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQLSELECT)) {

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return orders;
    }

    public Order updateTotalPrice(Order order, double totalPrice) {
        String sql = "UPDATE orders SET total_price = ? WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, totalPrice);
            statement.setObject(2, order.getId());

            statement.executeUpdate();

            return findById(order.getId()).stream().findFirst().orElse(null);
        }
        catch(SQLException e){
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return null;
    }

    public Order updateStatus(Order order, Status status) {
        String sql = "UPDATE orders SET status_id = ? WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, status.getId());
            statement.setObject(2, order.getId());

            statement.executeUpdate();

            return findById(order.getId()).stream().findFirst().orElse(null);
        }
        catch(SQLException e){
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return null;
    }

    public Order save(Order order) {
        String sql = "INSERT INTO orders (id, status_id, total_price, user_id) VALUES (?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();

            statement.setObject(1, id);
            statement.setObject(2, order.getStatus().getId());
            statement.setDouble(3, order.getTotalPrice());
            statement.setObject(4, order.getUser().getId());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch (SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return null;
    }

    public void delete(Order order) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, order.getId());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
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

        UUID statusId = (UUID) resultSet.getObject("status_id");
        String status_name = resultSet.getString("status_name");

        String username = resultSet.getString("user_username");
        String email = resultSet.getString("user_email");
        String password = resultSet.getString("user_password");
        Role role = Role.valueOf(resultSet.getString("user_role"));

        User user = new User(username, email, password);
        user.setId(userId);
        user.setRole(role);

        Status status = new Status(status_name);
        status.setId(statusId);

        Order order = new Order(user);

        order.setId(orderId);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);

        return order;
    }
}
