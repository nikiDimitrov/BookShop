package org.book.bookshop.configuration;

import org.book.bookshop.view.user.AdminView;
import org.book.bookshop.view.user.ClientView;
import org.book.bookshop.view.user.EmployeeView;
import org.book.bookshop.view.user.UserView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class UserViewConfiguration {

    @Bean
    public AdminView adminView(Scanner scanner){
        return new AdminView(scanner);
    }

    @Bean
    public EmployeeView employeeView(Scanner scanner) {
        return new EmployeeView(scanner);
    }

    @Bean
    public ClientView clientView(Scanner scanner) {
        return new ClientView(scanner);
    }

    @Bean
    public UserView userView() {
        return new UserView();
    }
}
