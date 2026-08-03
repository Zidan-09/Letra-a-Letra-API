package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.springframework.transaction.annotation.Transactional;

public class GoogleAuthUseCase implements UseCase<AuthInput, SignInOutput> {
    private final TokenService tokenService;
    private final NicknameService nicknameService;
    private final UserRepository userRepository;
    private final GoogleTokenService googleTokenService;

    public GoogleAuthUseCase(
            TokenService tokenService,
            NicknameService nicknameService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService
    ) {
        this.tokenService = tokenService;
        this.nicknameService = nicknameService;
        this.userRepository = userRepository;
        this.googleTokenService = googleTokenService;
    }

    @Override
    @Transactional
    public SignInOutput execute(AuthInput input) {
        GoogleAuthData payload = googleTokenService.verify(input.token());

        User user = userRepository.findByGoogleId(payload.googleId())
                .orElseGet(() -> {
                    String nickname = nicknameService.get();
                    User newUser = UserFactory.createGoogle(nickname, payload.email(), payload.googleId());
                    userRepository.save(newUser);
                    return newUser;
                });

        String token = tokenService.generateUserToken(user.getId());

        return new SignInOutput(user.getId(), token);
    }
}
