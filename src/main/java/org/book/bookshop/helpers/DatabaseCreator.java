package org.book.bookshop.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseCreator {
    private final Logger log = LoggerFactory.getLogger(DatabaseCreator.class);

    public boolean createTableFromScript(String scriptPath) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             InputStream inputStream = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                StringBuilder sql = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sql.append(line);
                }

                statement.execute(sql.toString());
                log.info("Database is created and it's functional.");
                return true;
            }
        } catch (Exception e) {
            log.error(String.format("Error in creating database! %s Terminating app!", e.getMessage()));
            return false;
        }
    }
}
