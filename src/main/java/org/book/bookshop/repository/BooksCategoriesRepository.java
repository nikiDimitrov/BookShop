package org.book.bookshop.repository;

import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BooksCategoriesRepository {

    private final String url = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private final String user = "postgres";
    private final String password = System.getenv("DB_PASSWORD");

    public void joinBookAndCategories(Book book) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for(Category category : book.getCategories()) {
                String sql = "INSERT INTO books_categories (book_id, categories_id) VALUES (?, ?)";

                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, book.getId());
                    statement.setObject(2, category.getId());

                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBookAndCategories(Book book) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            for(Category category : book.getCategories()) {
                String sql = "DELETE FROM books_categories WHERE book_id = ? AND categories_id = ?";

                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, book.getId());
                    statement.setObject(2, category.getId());

                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
