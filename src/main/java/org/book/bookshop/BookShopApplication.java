package org.book.bookshop;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BookShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    @EnableJpaRepositories
    public static class ConsoleRunner implements CommandLineRunner {
        @Autowired
        private final MainController mainController;

        @Override
        public void run(String... args) throws Exception {
            mainController.run();
        }
    }

}
