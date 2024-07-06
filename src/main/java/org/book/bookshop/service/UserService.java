package org.book.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.book.bookshop.exceptions.UserNotFoundException;
import org.book.bookshop.model.Role;
import org.book.bookshop.model.User;
import org.book.bookshop.repository.UserRepository;
import org.book.bookshop.exceptions.IncorrectInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public User registerUser(String username, String email, String password, Role role) throws IllegalArgumentException {
        if(userRepository.findByUsername(username).isPresent()
                || userRepository.findByEmail(email).isPresent())
        {
            throw new IllegalArgumentException("User with that username or email already exists!");
        }

        String encodedPassword = encoder.encode(password);

        User user = new User(username, email, encodedPassword);

        if(userRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
        }
        else {
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    public User loginUser(String username, String password) throws UsernameNotFoundException, IncorrectInputException {
        User user = loadUserByUsername(username);

        if(user == null) {
            throw new UserNotFoundException(String.format("User with username %s not found!", username));
        }

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
