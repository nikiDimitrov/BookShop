package org.book.bookshop.controller;

import org.book.bookshop.repository.UserRepository;
import org.book.bookshop.service.UserService;
import org.book.bookshop.view.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    private UserView userView;


    public void run() {
        UserView userView = new UserView(userController);

        userView.run();
    }
}
