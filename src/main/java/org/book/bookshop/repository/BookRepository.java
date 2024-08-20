package org.book.bookshop.repository;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {

    private final String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private final String user = "postgres";
    private final String password = System.getenv("DB_PASSWORD");


    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)){

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Book book = mapResultSetToBook(resultSet, connection);
                books.add(book);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public Optional<Book> findById(UUID bookId) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, bookId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Book book = mapResultSetToBook(resultSet, connection);

                return Optional.of(book);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void updateQuantity(Book book, int quantity) {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setObject(2, book.getId());

            statement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public Book save(Book book) {
        String sql = "INSERT INTO books (id, name, author, price, year, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID bookId = UUID.randomUUID();
            statement.setObject(1, bookId);
            statement.setString(2, book.getName());
            statement.setString(3, book.getAuthor());
            statement.setDouble(4, book.getPrice());
            statement.setInt(5, book.getYear());
            statement.setInt(6, book.getQuantity());

            statement.executeUpdate();

            return findById(bookId).stream().findFirst().orElse(null);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Book book) {
        String sql = "DELETE FROM books WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, book.getId());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private Book mapResultSetToBook(ResultSet resultSet, Connection connection) throws SQLException {
        UUID bookId = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");
        String author = resultSet.getString("author");
        double price = resultSet.getDouble("price");
        List<Category> categories = fetchCategoriesForBook(bookId, connection);
        int year = resultSet.getInt("year");
        int quantity = resultSet.getInt("quantity");

        Book book = new Book(name, author, price, categories, year, quantity);
        book.setId(bookId);

        return book;
    }

    private List<Category> fetchCategoriesForBook(UUID bookId, Connection connection) throws SQLException {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT c.id, c.name FROM categories c " +
                "JOIN books_categories bc ON c.id = bc.categories_id " +
                "WHERE bc.book_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID categoryId = (UUID) resultSet.getObject("id");
                    String categoryName = resultSet.getString("name");
                    Category category = new Category(categoryName);
                    category.setId(categoryId);
                    categories.add(category);
                }
            }
        }

        return categories;
    }

}
