package org.book.bookshop.repository;

import org.book.bookshop.helpers.DatabaseConnection;
import org.book.bookshop.model.Category;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryRepository {

    public Optional<Category> findCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Category category = mapResultSetToCategory(resultSet);
                return Optional.of(category);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Category> findById(UUID id) {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Category category = mapResultSetToCategory(resultSet);

                return Optional.of(category);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
    public Category save(Category category) {
        String sql = "INSERT INTO categories (id, name) VALUES (?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();
            statement.setObject(1, id);
            statement.setString(2, category.getName());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveBatch(List<Category> categories) {
        for(Category category : categories) {
            save(category);
        }
    }

    public void delete(Category category) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, category.getId());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBatch(List<Category> categories) {
        for(Category category : categories) {
            delete(category);
        }
    }

    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException{
        UUID id = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");

        Category category = new Category(name);
        category.setId(id);
        return category;
    }
}
