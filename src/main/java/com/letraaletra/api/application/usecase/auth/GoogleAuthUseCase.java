package com.letraaletra.api.application.usecase.auth;

import com.letraaletra.api.application.command.auth.AuthCommand;
import com.letraaletra.api.application.context.GoogleAuthData;
import com.letraaletra.api.application.output.user.SignInOutput;
import com.letraaletra.api.application.port.GoogleTokenService;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public class GoogleAuthUseCase {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final GoogleTokenService googleTokenService;

    public GoogleAuthUseCase(TokenService tokenService, UserRepository userRepository, GoogleTokenService googleTokenService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.googleTokenService = googleTokenService;
    }

    public SignInOutput execute(AuthCommand command) {
        GoogleAuthData payload = googleTokenService.verify(command.token());

        Optional<User> userOpt = userRepository.findByGoogleId(payload.googleId());

        User user = userOpt.orElseGet(() ->
                userRepository.save(
                        new User(
                            UUID.randomUUID().toString(),
                    null,
                    null,
                            payload.email(),
                            null,
                            payload.googleId()
                    )
                )
        );

        String token = tokenService.generateToken(user.getId());

        return buildOutput(user.getId(), token);
    }

    private SignInOutput buildOutput(String id, String token) {
        return new SignInOutput(id, token);
    }
}
