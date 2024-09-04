package org.book.bookshop.controller;

import org.book.bookshop.controller.user.LoginController;
import org.book.bookshop.controller.user.UserController;
import org.book.bookshop.factory.UserControllerFactory;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.UserView;


public class MainController {
     
    private final LoginController loginController;
    private final UserControllerFactory userControllerFactory;
    private final UserView userView;

    public MainController() {
        this.loginController = new LoginController();
        this.userControllerFactory = new UserControllerFactory();
        this.userView = new UserView();
    }

    public void run(){

        User user = loginController.run();

        while(user == null) {
            user = loginController.run();
        }

        UserController controller = userControllerFactory.getController(user.getRole());

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
