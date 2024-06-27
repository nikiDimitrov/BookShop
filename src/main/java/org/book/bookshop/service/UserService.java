package org.book.bookshop.service;

import org.book.bookshop.model.User;
import org.book.bookshop.utilities.WrongPasswordException;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User loginUser(String username, String password) throws WrongPasswordException;
    List<User> findAllUsers();
    User loadUserByUsername(String username);
    User loadUserByEmail(String email);
    void deleteUserByUsername(String username);
    void deleteUserByEmail(String email);
}
