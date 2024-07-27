package org.book.bookshop.controller;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.controller.user.LoginController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.factory.UserControllerFactory;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.UserView;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final LoginController loginController;
    private final UserControllerFactory userControllerFactory;
    private final UserView userView;

    public void run(){

        User user = loginController.run();

        while(user == null) {
            user = loginController.run();
        }

        UserController controller = userControllerFactory.getController(user);

        userView.intro(user);

        int input = controller.run(user);

        while(input != 0) {
            if(input == -1) {
                userView.displayError("Argument is not a number! Type a number from the list!");
            }

            userView.awaitEnter();
            input = controller.run(user);
        }

        userView.displayExitMessage(user);
    }
}
