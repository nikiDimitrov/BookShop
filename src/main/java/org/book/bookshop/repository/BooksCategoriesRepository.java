package org.book.bookshop.repository;

import org.book.bookshop.helpers.DatabaseConnection;
import org.book.bookshop.model.Book;
import org.book.bookshop.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooksCategoriesRepository {

    public void joinBookAndCategories(Book book) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            for (Category category : book.getCategories()) {
                String sql = "INSERT INTO books_categories (book_id, categories_id) VALUES (?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, book.getId());
                    statement.setObject(2, category.getId());

                    statement.executeUpdate();
                }
            }
        }
    }

    public void deleteBookAndCategories(Book book) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            for (Category category : book.getCategories()) {
                String sql = "DELETE FROM books_categories WHERE book_id = ? AND categories_id = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setObject(1, book.getId());
                    statement.setObject(2, category.getId());

                    statement.executeUpdate();
                }
            }
        }
    }
}
