package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.UserRepository;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    public User registerUser(User user) throws IllegalArgumentException {
        if(userRepository.findByUsername(user.getUsername()).isPresent()
                && userRepository.findByEmail(user.getEmail()).isPresent())
        {
            throw new IllegalArgumentException("User already exists!");
        }
        encoder = new BCryptPasswordEncoder(16);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) throws IncorrectInputException {
        User user = loadUserByUsername(username);
        if(!encoder.matches(password, user.getPassword())){
            throw new IncorrectInputException(String.format("Password for %s is wrong!", username));
        }

        return user;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User loadUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.stream().findFirst().orElse(null);

    }

    public User loadUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        return optionalUser.stream().findFirst().orElse(null);
    }

}
