package org.book.bookshop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class ScannerConfiguration {

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
