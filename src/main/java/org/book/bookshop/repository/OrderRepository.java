package org.book.bookshop.repository;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final UserRepository userRepository;

    private String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private String username = "postgres";
    private String password = System.getenv("DB_PASSWORD");

    public List<Order> findByUserAndStatus(User user, String status) {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders WHERE user_id = ? AND status = ?";

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
        String sql = "SELECT * FROM orders WHERE id = ?";

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

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders";

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
        UUID id = (UUID) resultSet.getObject("id");
        UUID user_id = (UUID) resultSet.getObject("user_id");
        double totalPrice = resultSet.getDouble("total_price");
        String status = resultSet.getString("status");

        User user = userRepository.findById(user_id).orElse(null);
        Order order = new Order(user);

        order.setId(id);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);

        return order;
    }
}
