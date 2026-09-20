package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.input.IssueSessionInput;
import com.letraaletra.api.features.user.application.output.GoogleAuthData;
import com.letraaletra.api.features.user.application.output.IssueSessionOutput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;

public class GoogleAuthUseCase implements UseCase<AuthInput, SignInOutput> {
    private final TokenService tokenService;
    private final NicknameService nicknameService;
    private final UserRepository userRepository;
    private final GoogleTokenService googleTokenService;
    private final UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase;

    public GoogleAuthUseCase(
            TokenService tokenService,
            NicknameService nicknameService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService,
            UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase
    ) {
        this.tokenService = tokenService;
        this.nicknameService = nicknameService;
        this.userRepository = userRepository;
        this.googleTokenService = googleTokenService;
        this.issueSessionUseCase = issueSessionUseCase;
    }

    @Override
    public SignInOutput execute(AuthInput input) {
        GoogleAuthData payload = googleTokenService.verify(input.token());

        User user = userRepository.findByGoogleId(payload.googleId())
                .orElseGet(() -> {
                    String nickname = nicknameService.get();
                    return UserFactory.createGoogle(
                            nickname,
                            payload.email(),
                            payload.googleId()
                    );
                });

        if (user.isBanned()) {
            throw new UserBannedFromGameException();
        }

        userRepository.save(user);

        IssueSessionOutput session = issueSessionUseCase.execute(new IssueSessionInput(user.getUserId()));
        String token = tokenService.generateUserToken(user.getUserId(), user.getTokenVersion());

        return new SignInOutput(user.getUserId(), token, session.refreshToken());
    }
}
