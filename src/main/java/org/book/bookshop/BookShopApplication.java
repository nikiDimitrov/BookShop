package org.book.bookshop;

import org.book.bookshop.controller.MainController;
import org.book.bookshop.helpers.DatabaseCreator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
        DatabaseCreator databaseCreator = new DatabaseCreator();

        boolean tableCreated = databaseCreator.createTableFromScript("tables.sql");

        if(tableCreated) {
            ConsoleRunner consoleRunner = new ConsoleRunner();
            consoleRunner.run();
        }
    }

    public static class ConsoleRunner implements CommandLineRunner {
        private final MainController mainController;

        public ConsoleRunner() {
            this.mainController = new MainController();
        }

        @Override
        public void run(String... args) {
            mainController.run();
        }
    }

}
