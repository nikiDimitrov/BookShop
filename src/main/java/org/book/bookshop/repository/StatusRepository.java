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

    public Optional<Status> findStatusByName(String name) {
        String sql = "SELECT * FROM statuses WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Status status = mapResultSetToStatus(resultSet);

                return Optional.of(status);
            }
        }
        catch(SQLException e){
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return Optional.empty();
    }

    public Optional<Status> findById(UUID id) {
        String sql = "SELECT * FROM statuses WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Status status = mapResultSetToStatus(resultSet);

                return Optional.of(status);
            }
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return Optional.empty();
    }

    public Status save(Status status) {
        String sql = "INSERT INTO statuses (id, name) VALUES (?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();
            statement.setObject(1, id);
            statement.setString(2, status.getName());

            statement.executeUpdate();

            return findById(id).stream().findFirst().orElse(null);
        }
        catch(SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
        }

        return null;
    }

    public void delete(Status status) {
        String sql = "DELETE FROM statuses WHERE id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, status.getId());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            DatabaseConnection.checkIfConnectionErrorAndTerminateOrLog(e);
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
