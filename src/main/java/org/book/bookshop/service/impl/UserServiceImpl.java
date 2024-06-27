package org.book.bookshop.service.impl;

import org.book.bookshop.model.User;
import org.book.bookshop.repository.UserRepository;
import org.book.bookshop.service.UserService;
import org.book.bookshop.utilities.WrongPasswordException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder(16);
    }

    @Override
    public User registerUser(User user) throws IllegalArgumentException {
        if(userRepository.findByUsername(user.getUsername()).isPresent()
                && userRepository.findByEmail(user.getEmail()).isPresent())
        {
            throw new IllegalArgumentException("User already exists!");
        }
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String username, String password) throws WrongPasswordException {
        User user = loadUserByUsername(username);
        if(!encoder.matches(password, user.getPassword())){
            throw new WrongPasswordException(String.format("Password for %s is wrong!", username));
        }

        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User loadUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.stream().findFirst().orElse(null);

    }

    @Override
    public User loadUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        return optionalUser.stream().findFirst().orElse(null);
    }

    @Override
    public void deleteUserByUsername(String username) {
        userRepository.deleteUserByUsername(username);
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }
}
