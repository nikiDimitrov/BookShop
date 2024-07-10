package org.book.bookshop.controller;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.user.LoginController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.factory.UserControllerFactory;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    private final LoginController loginController;
    private final UserControllerFactory userControllerFactory;
    private final UserView userView;

    public void run() throws IOException {
        User user = loginController.run();
        UserController controller = userControllerFactory.getController(user);
        userView.intro(user);

        int input = controller.run();

        while(input != 0) {
            userView.awaitEnter();
            input = controller.run();
        }
        userView.onExit(user);
    }
}
