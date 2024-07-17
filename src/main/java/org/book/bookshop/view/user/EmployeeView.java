package org.book.bookshop.view.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class EmployeeView extends UserView {

    private final Scanner scanner;

    public String employeeOptions() {
        return null;
    }
}
