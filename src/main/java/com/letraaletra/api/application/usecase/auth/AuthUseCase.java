package com.letraaletra.api.application.usecase.auth;

import com.letraaletra.api.application.command.auth.AuthCommand;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;

import java.util.UUID;

public class AuthUseCase {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthUseCase(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public SignInOutput execute(AuthCommand command) {
        User user = userRepository.findByGoogleId(command.googleId())
                .orElseGet(() -> {
                    String userId = UUID.randomUUID().toString();

                    User newUser = new User(
                            userId,
                            null,
                            null,
                            command.email(),
                            null,
                            command.googleId()
                    );

                    userRepository.save(newUser);
                    return newUser;
                });

        String token = tokenService.generateToken(user.getId());

        return buildOutput(user.getId(), token);
    }

    private SignInOutput buildOutput(String id, String token) {
        return new SignInOutput(id, token);
    }
}
