package org.book.bookshop.repository;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Order;
import org.book.bookshop.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    private String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private String user = "postgres";
    private String password = System.getenv("DB_PASSWORD");

    public Optional<OrderItem> findById(UUID id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                OrderItem orderItem = mapResultSetToOrderItem(resultSet);
                return Optional.of(orderItem);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<OrderItem> findByOrderId(UUID orderId) {
        List<OrderItem> orderItems = new ArrayList<>();

        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OrderItem orderItem = mapResultSetToOrderItem(resultSet);
                orderItems.add(orderItem);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return orderItems;
    }

    public OrderItem save(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (id, quantity, book_id, order_id) VALUES (?, ?, ? ,?)";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();
            statement.setObject(1, id);
            statement.setInt(2, orderItem.getQuantity());
            statement.setObject(3, orderItem.getBook().getId());
            statement.setObject(4, orderItem.getOrder().getId());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(OrderItem orderItem) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, orderItem.getId());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInBatch(List<OrderItem> orderItems) {
        for(OrderItem orderItem : orderItems) {
            delete(orderItem);
        }
    }

    private OrderItem mapResultSetToOrderItem(ResultSet resultSet) throws SQLException {
        UUID id = (UUID) resultSet.getObject("id");
        UUID book_id = (UUID) resultSet.getObject("book_id");
        UUID order_id = (UUID) resultSet.getObject("order_id");
        int quantity = resultSet.getInt("quantity");

        Book book = bookRepository.findById(book_id).stream().findFirst().orElse(null);
        Order order = orderRepository.findById(order_id).stream().findFirst().orElse(null);

        OrderItem orderItem = new OrderItem(order, book, quantity);
        orderItem.setId(id);

        return orderItem;
    }
}
