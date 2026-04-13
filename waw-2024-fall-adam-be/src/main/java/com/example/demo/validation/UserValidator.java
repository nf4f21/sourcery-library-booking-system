package com.example.demo.validation;

import com.example.demo.exception.UserException;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAvailableEmail(String email) {
        userRepository.findByEmail(email).ifPresent(
                u -> {
                    throw new UserException(email + " this email is already in use");
                }
        );
        return true;
    }

}
