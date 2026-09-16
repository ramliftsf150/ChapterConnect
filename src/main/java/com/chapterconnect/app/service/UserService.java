package com.chapterconnect.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chapterconnect.app.exception.DuplicateResourceException;
import com.chapterconnect.app.model.AccountStatus;
import com.chapterconnect.app.model.Role;
import com.chapterconnect.app.model.User;
import com.chapterconnect.app.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(
            String email,
            String passwordHash,
            Role role,
            AccountStatus accountStatus) {

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "A user with this email already exists."
            );
        }

        User user = new User(
                email,
                passwordHash,
                role,
                accountStatus
        );

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with id: " + id
                        )
                );
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}