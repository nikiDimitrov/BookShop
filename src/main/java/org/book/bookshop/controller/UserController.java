package org.book.bookshop.controller;
import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService service;

    public void insertUser(String username, String email, String password) throws IllegalArgumentException
    {
        List<User> allUsers = service.findAllUsers();

        User user = new User(username, email, password);

        if(allUsers.isEmpty()){
            user.setRole(Role.ADMIN);
        }
        else {
            user.setRole(Role.CLIENT);
        }
        service.registerUser(user);
    }

    public User loginUser(String username, String password) throws IncorrectInputException, UserNotFoundException {
        return service.loginUser(username, password);
    }
}
