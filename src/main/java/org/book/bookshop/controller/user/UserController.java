package org.book.bookshop.controller.user;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.User;
import org.book.bookshop.view.user.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserView view;
    private final AdminController adminController;

    public void run(User user){
        view.intro(user);

        int input = 0;

        switch(user.getRole()) {
            case ADMIN -> {
                try {
                    input = Integer.parseInt(view.adminOptions());
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
                
                if(input != 0) {
                    adminController.run(input);
                    this.run(user);
                }
            }
            case EMPLOYEE -> {
                input = view.employeeOptions();
                //employee();
            }
            case CLIENT -> {
                input = view.clientOptions();
                //client();
            }

        }
    }
}
