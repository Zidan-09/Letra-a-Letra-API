package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.domain.exception.UserBannedFromGameException;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;

public class AuthUserUseCase implements UseCase<SignInInput, SignInOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthUserUseCase(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    @Override
    public SignInOutput execute(SignInInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(UserNotFoundException::new);

        if (user.isBanned()) {
            throw new UserBannedFromGameException();
        }

        checkMatch(input.password(), user.getPasswordHash());

        String token = tokenService.generateUserToken(user.getUserId());

        return new SignInOutput(user.getUserId(), token);
    }

    private void checkMatch(String password, String hash) {
        boolean matches = passwordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }
}
