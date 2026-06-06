package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

public class SignInUseCase implements UseCase<SignInInput, SignInOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public SignInUseCase(UserRepository userRepository, PasswordService passwordService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public SignInOutput execute(SignInInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(UserNotFoundException::new);

        checkMatch(input.password(), user.getHashPassword());

        String token = tokenService.generateToken(user.getId());

        return buildReturn(user.getId(), token);
    }

    private void checkMatch(String password, String hash) {
        boolean matches = passwordService.matches(password, hash);

        if (!matches) {
            throw new InvalidPasswordException();
        }
    }

    private SignInOutput buildReturn(String userId, String token) {
        return new SignInOutput(
                userId,
                token
        );
    }
}
