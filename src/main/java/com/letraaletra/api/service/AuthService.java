package com.letraaletra.api.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.request.user.LoginRequestDTO;
import com.letraaletra.api.dto.response.user.LoginResponseDTO;
import com.letraaletra.api.exception.exceptions.InvalidPasswordException;
import com.letraaletra.api.exception.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email());

        if (user == null) {
            throw new UserNotFoundException();
        }

        boolean matches = passwordService.matches(request.password(), user.getPassword());

        if (!matches) {
            throw new InvalidPasswordException();
        }

        String token = tokenService.generateToken(user.getId());

        return new LoginResponseDTO(
               token
        );
    }
}
