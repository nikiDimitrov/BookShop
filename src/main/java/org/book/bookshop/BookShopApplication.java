package org.book.bookshop;

import org.book.bookshop.controller.MainController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class BookShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
        ConsoleRunner consoleRunner = new ConsoleRunner();
        consoleRunner.run();
    }

    @EnableJpaRepositories
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
