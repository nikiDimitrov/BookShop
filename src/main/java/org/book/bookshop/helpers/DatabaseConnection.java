package org.book.bookshop.helpers;

import org.postgresql.util.PSQLState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String URL = "jdbc:postgresql://localhost:5432/bookshop?stringtype=unspecified";
    private static final String USER = "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void checkIfConnectionErrorAndTerminateOrLog(SQLException e) {
        if(PSQLState.isConnectionError(e.getSQLState())) {
            log.error(String.format("There is an error in the database connection. %s Terminating app...", e.getMessage()));
            System.exit(-1);
        }
        else {
            log.warn(e.getMessage());
        }
    }
}
