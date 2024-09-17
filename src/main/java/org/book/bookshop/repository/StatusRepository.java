package org.book.bookshop.repository;

import org.book.bookshop.helpers.DatabaseConnection;
import org.book.bookshop.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class StatusRepository {

    public Optional<Status> findStatusByName(String name) throws SQLException {
        String sql = "SELECT * FROM statuses WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Status status = mapResultSetToStatus(resultSet);
                return Optional.of(status);
            }
        }

        return Optional.empty();
    }

    public Optional<Status> findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM statuses WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Status status = mapResultSetToStatus(resultSet);
                return Optional.of(status);
            }
        }

        return Optional.empty();
    }

    public Status save(Status status) throws SQLException {
        String sql = "INSERT INTO statuses (id, name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();
            statement.setObject(1, id);
            statement.setString(2, status.getName());

            statement.executeUpdate();
            return findById(id).orElse(null);
        }
    }

    public void delete(Status status) throws SQLException {
        String sql = "DELETE FROM statuses WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, status.getId());
            statement.executeUpdate();
        }
    }

    private Status mapResultSetToStatus(ResultSet resultSet) throws SQLException {
        UUID id = (UUID) resultSet.getObject("id");
        String name = resultSet.getString("name");

        Status status = new Status(name);
        status.setId(id);

        return status;
    }
}
