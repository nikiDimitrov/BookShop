package org.book.bookshop.helpers;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseCreator {

    public void createTableFromScript(String scriptPath) {
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
                System.out.println("Database is ready.");

            }
        } catch (Exception e) {
            //temporary
            e.printStackTrace();
        }
    }
}
