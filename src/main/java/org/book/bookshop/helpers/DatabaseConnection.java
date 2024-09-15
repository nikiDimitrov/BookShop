package org.book.bookshop.helpers;

import org.postgresql.util.PSQLState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnection.class);

    private static String URL;
    private static String USER;
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    static {
        loadDatabaseProperties();
    }

    private static void loadDatabaseProperties() {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties properties = new Properties();

            if (input == null) {
                log.error("Sorry, unable to find database.properties. Can't connect.");
                return;
            }

            properties.load(input);

            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");

            log.info("Database connection properties loaded successfully.");
        } catch (IOException e) {
            log.error("Error loading database properties: {}", e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void checkIfConnectionErrorAndTerminateOrLog(SQLException e) {
        if(PSQLState.isConnectionError(e.getSQLState())) {
            log.error(String.format("There is an error in the database connection. %s Terminating app...", e.getMessage()));
            System.exit(-1);
        } else {
            log.error("SQL Error occurred: {}", e.getMessage());
        }
    }

}
