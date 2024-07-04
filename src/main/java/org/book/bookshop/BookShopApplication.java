package org.book.bookshop;

import org.book.bookshop.controller.MainController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableJpaRepositories
public class BookShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

    @Component
    public static class ConsoleRunner implements CommandLineRunner {
        private final MainController mainController;

        public ConsoleRunner() {
            mainController = new MainController();
        }

        @Override
        public void run(String... args) throws Exception {
            mainController.run();
        }
    }

}
