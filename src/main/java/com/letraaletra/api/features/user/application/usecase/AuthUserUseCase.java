package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.IssueSessionInput;
import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.IssueSessionOutput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidPasswordException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

public class AuthUserUseCase implements UseCase<SignInInput, SignInOutput> {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase;

    public AuthUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService,
            UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.issueSessionUseCase = issueSessionUseCase;
    }

    @Override
    public SignInOutput execute(SignInInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(UserNotFoundException::new);

        if (user.isBanned()) {
            throw new UserBannedFromGameException();
        }

        checkMatch(input.password(), user.getPasswordHash());

        IssueSessionOutput session = issueSessionUseCase.execute(new IssueSessionInput(user.getUserId()));
        String token = tokenService.generateUserToken(user.getUserId(), user.getTokenVersion());

        userRepository.save(user);

        return new SignInOutput(user.getUserId(), token, session.refreshToken());
    }

    private void checkMatch(String password, String hash) {
        if (hash == null || !passwordService.matches(password, hash)) {
            throw new InvalidPasswordException();
        }
    }
}
