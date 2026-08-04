package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.domain.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.SamePasswordException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.springframework.transaction.annotation.Transactional;

public class ResetPasswordUseCase implements UseCase<ResetPasswordInput, Void> {
    private final UserRepository userRepository;
    private final TokenHashService tokenHashService;
    private final PasswordService passwordService;
    private final ResetCodeRepository codeRepository;

    public ResetPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            ResetCodeRepository codeRepository
    ) {
        this.userRepository = userRepository;
        this.tokenHashService = tokenHashService;
        this.passwordService = passwordService;
        this.codeRepository = codeRepository;
    }

    @Override
    @Transactional
    public Void execute(ResetPasswordInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(UserNotFoundException::new);

        PasswordResetCode resetCode =
                codeRepository.findLatestByUserId(user.getUserId())
                        .orElseThrow(InvalidTokenException::new);

        resetCode.validate(input.code(), tokenHashService);

        if (passwordService.matches(input.newPassword(), user.getPasswordHash())) {
            throw new SamePasswordException();
        }

        resetCode.markAsUsed();

        user.changePassword(passwordService.hash(input.newPassword()));

        userRepository.save(user);
        codeRepository.save(resetCode);

        return null;
    }
}
