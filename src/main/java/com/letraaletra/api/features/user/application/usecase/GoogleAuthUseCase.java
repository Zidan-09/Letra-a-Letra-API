package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.TokenService;
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

    public SignInOutput execute(AuthInput input) {
        GoogleAuthData payload = googleTokenService.verify(input.token());

        Optional<User> userOpt = userRepository.findByGoogleId(payload.googleId());

        User user = userOpt.orElseGet(() -> {
            User userFabricated = userFactory.createGoogle(
                    payload.email(),
                    payload.googleId()
            );

            userRepository.save(userFabricated);

            return userFabricated;
        });

        String token = tokenService.generateUserToken(user.getId());

        return buildOutput(user.getId(), token);
    }

    private SignInOutput buildOutput(UUID id, String token) {
        return new SignInOutput(id, token);
    }
}
