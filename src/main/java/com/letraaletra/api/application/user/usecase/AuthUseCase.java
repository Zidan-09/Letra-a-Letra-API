package com.letraaletra.api.application.user.usecase;

import com.letraaletra.api.application.user.service.PasswordService;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.request.user.LoginRequestDTO;
import com.letraaletra.api.presentation.dto.response.user.LoginResponseDTO;
import com.letraaletra.api.domain.user.exceptions.InvalidPasswordException;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCase {

    @Autowired
    private GlobalTokenService globalTokenService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email());

        validateUser(user);

        checkMatch(request.password(), user.getPassword());

        String token = globalTokenService.generateToken(user.getId());

        return buildResponse(user.getId(), token);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void checkMatch(String password, String hash) {
        boolean matches = passwordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }

    private LoginResponseDTO buildResponse(String userId, String token) {
        return new LoginResponseDTO(
                userId,
                token
        );
    }
}
