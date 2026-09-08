package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.domain.reset.PasswordResetCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.reset.exception.SamePasswordException;
import com.letraaletra.api.features.user.domain.reset.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.util.UUID;

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
    public Void execute(ResetPasswordInput input) {
        String codeHash = tokenHashService.hash(input.code());

        PasswordResetCode resetCode =
                codeRepository.findByCodeHash(codeHash)
                        .orElseThrow(InvalidTokenException::new);

        resetCode.validate(codeHash);

        User user = userRepository.find(resetCode.getUserId())
                .orElseThrow(InvalidTokenException::new);

        if (passwordService.matches(input.newPassword(), user.getPasswordHash())) {
            throw new SamePasswordException();
        }

        resetCode.markAsUsed();

        user.changePassword(passwordService.hash(input.newPassword()));
        user.setTokenVersion(UUID.randomUUID());

        userRepository.save(user);
        codeRepository.save(resetCode);

        return null;
    }
}
