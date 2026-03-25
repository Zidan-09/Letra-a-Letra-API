package com.letraaletra.api.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.exception.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String userId, String rawPassword) {
        User user = userRepository.find(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        return encoder.matches(rawPassword, user.getPassword());
    }
}
