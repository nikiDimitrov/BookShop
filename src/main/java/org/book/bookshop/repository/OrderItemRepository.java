package org.book.bookshop.repository;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderItemRepository {

    private String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private String user = "postgres";
    private String password = System.getenv("DB_PASSWORD");
    private String sqlSelect = "SELECT oi.id AS order_item_id, oi.quantity AS order_item_quantity, " +
            "o.id AS order_id, o.total_price AS order_total_price, o.status AS order_status, " +
            "b.id AS book_id, b.name AS book_name, b.author AS book_author, b.price AS book_price, b.year AS book_year, b.quantity as book_quantity, " +
            " u.id AS user_id, u.username AS user_username, u.email AS user_email, u.password AS user_password, u.role AS user_role, " +
            "c.id AS category_id, c.name AS category_name FROM order_items as oi " +
            "JOIN orders as o on oi.order_id = o.id " +
            "JOIN books as b on oi.book_id = b.id " +
            "JOIN users as u on o.user_id = u.id " +
            "JOIN books_categories as bc on bc.book_id = b.id " +
            "JOIN categories as c on c.id = bc.categories_id";

    public Optional<OrderItem> findById(UUID id) {
        String sql = sqlSelect +
                " WHERE oi.id = ?";

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

        String sql = sqlSelect + " WHERE order_id = ?";

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
        UUID orderItemId = (UUID) resultSet.getObject("order_item_id");
        UUID bookId = (UUID) resultSet.getObject("book_id");
        UUID orderId = (UUID) resultSet.getObject("order_id");
        int orderItemQuantity = resultSet.getInt("order_item_quantity");

        String author = resultSet.getString("book_author");
        String name = resultSet.getString("book_name");
        double price = resultSet.getDouble("book_price");
        int bookQuantity = resultSet.getInt("book_quantity");
        int year = resultSet.getInt("book_year");

        UUID userId = (UUID) resultSet.getObject("user_id");
        String status = resultSet.getString("order_status");
        double orderPrice = resultSet.getDouble("order_total_price");

        String email = resultSet.getString("user_email");
        String username = resultSet.getString("user_username");
        String password = resultSet.getString("user_password");
        Role role = Role.valueOf(resultSet.getString("user_role"));

        User user = new User(username, email, password);
        user.setId(userId);
        user.setRole(role);

        Order order = new Order(user);
        order.setId(orderId);
        order.setStatus(status);
        order.setTotalPrice(orderPrice);

        Book book = new Book(name, author, price, new ArrayList<>(), year, bookQuantity);
        book.setId(bookId);

        List<Category> categories = new ArrayList<>();
        do {
            UUID categoryId = (UUID) resultSet.getObject("category_id");
            String categoryName = resultSet.getString("category_name");
            Category category = new Category(categoryName);
            category.setId(categoryId);
            categories.add(category);
        } while (resultSet.next() && resultSet.getObject("order_item_id").equals(orderItemId));

        book.setCategories(categories);

        OrderItem orderItem = new OrderItem(order, book, orderItemQuantity);
        orderItem.setId(orderItemId);

        return orderItem;
    }

}
