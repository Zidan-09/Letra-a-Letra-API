package com.letraaletra.api.application.usecase.auth;

import com.letraaletra.api.application.command.auth.AuthInput;
import com.letraaletra.api.application.context.GoogleAuthData;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.application.port.GoogleTokenService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;

import java.util.Optional;
import java.util.UUID;

public class GoogleAuthUseCase implements UseCase<AuthInput, SignInOutput> {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final GoogleTokenService googleTokenService;
    private final UserFactory userFactory;

    public GoogleAuthUseCase(
            TokenService tokenService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService,
            UserFactory userFactory
    ) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.googleTokenService = googleTokenService;
        this.userFactory = userFactory;
    }

    public SignInOutput execute(AuthInput command) {
        GoogleAuthData payload = googleTokenService.verify(command.token());

        Optional<User> userOpt = userRepository.findByGoogleId(payload.googleId());

        User user = userOpt.orElseGet(() ->
                userRepository.save(
                        userFactory.createGoogle(UUID.randomUUID().toString(), payload.email(), payload.googleId())
                )
        );

        String token = tokenService.generateToken(user.getId());

        return buildOutput(user.getId(), token);
    }

    private SignInOutput buildOutput(String id, String token) {
        return new SignInOutput(id, token);
    }
}
