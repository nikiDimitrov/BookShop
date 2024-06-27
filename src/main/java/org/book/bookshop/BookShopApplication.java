package org.book.bookshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BookShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookShopApplication.class, args);
	}

	@Component
	public static class ConsoleRunner implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {
			System.out.println("Hello World!");
		}
	}

}
