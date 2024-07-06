package org.book.bookshop.controller;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.user.LoginController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    private final LoginController loginController;
    private final UserController userController;

    public void run() {
        User user = loginController.run();
        userController.run(user);
    }
}
