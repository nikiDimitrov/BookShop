package org.book.bookshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    private final UserController userController;

    public void run() {
        userController.run();
    }
}
