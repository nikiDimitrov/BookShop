package org.book.bookshop.repository;

import org.book.bookshop.helpers.DatabaseConnection;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;

import java.sql.*;
import java.util.*;

public class BookRepository {

    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        Map<UUID, List<Category>> bookCategoriesMap = new HashMap<>();

        String bookSql = "SELECT * FROM books";
        String categorySql = "SELECT bc.book_id, c.* FROM books_categories AS bc " +
                "JOIN categories as c ON bc.categories_id = c.id where bc.book_id IN (SELECT id FROM books)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement bookStatement = connection.prepareStatement(bookSql);
            PreparedStatement categoryStatement = connection.prepareStatement(categorySql);
            ResultSet bookResultSet = bookStatement.executeQuery();
            ResultSet categoryResultSet = categoryStatement.executeQuery()){

            while (bookResultSet.next()) {
                Book book = mapResultSetToBook(bookResultSet);
                books.add(book);
            }

            while (categoryResultSet.next()) {
                UUID bookId = (UUID) categoryResultSet.getObject("book_id");
                UUID categoryId = (UUID) categoryResultSet.getObject("id");
                String categoryName = categoryResultSet.getString("name");
                Category category = new Category(categoryName);
                category.setId(categoryId);

                bookCategoriesMap.computeIfAbsent(bookId, k-> new ArrayList<>()).add(category);
            }

            for (Book book : books) {
                book.setCategories(bookCategoriesMap.getOrDefault(book.getId(), new ArrayList<>()));
            }
        }
        catch (SQLException e) {
            //to remove
            e.printStackTrace();
        }

        return books;
    }

    public Optional<Book> findById(UUID bookId) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setObject(1, bookId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Book book = mapResultSetToBook(resultSet);
                getCategoriesForBook(book);
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

        try(Connection connection = DatabaseConnection.getConnection();
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

        try(Connection connection = DatabaseConnection.getConnection();
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

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, book.getId());

            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
        UUID bookId = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");
        String author = resultSet.getString("author");
        double price = resultSet.getDouble("price");
        int year = resultSet.getInt("year");
        int quantity = resultSet.getInt("quantity");

        Book book = new Book(name, author, price, new ArrayList<>(), year, quantity);
        book.setId(bookId);

        return book;
    }

    private void getCategoriesForBook(Book book) {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT bc.book_id, c.* FROM books_categories AS bc" +
                " JOIN categories as c ON bc.categories_id = c.id WHERE bc.book_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, book.getId());

            ResultSet categoryResultSet = statement.executeQuery();

            while (categoryResultSet.next()) {
                UUID categoryId = (UUID) categoryResultSet.getObject("id");
                String categoryName = categoryResultSet.getString("name");
                Category category = new Category(categoryName);
                category.setId(categoryId);

                categories.add(category);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        book.setCategories(categories);
    }

}
