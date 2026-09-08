package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.reset.PasswordResetCode;
import com.letraaletra.api.features.user.domain.reset.repository.ResetCodeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;

public class VerifyResetCodeUseCase implements UseCase<VerifyResetCodeInput, Void> {
    private final UserRepository userRepository;
    private final ResetCodeRepository codeRepository;
    private final TokenHashService tokenHashService;

    public VerifyResetCodeUseCase(
            UserRepository userRepository,
            ResetCodeRepository codeRepository,
            TokenHashService tokenHashService
    ) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.tokenHashService = tokenHashService;
    }

    @Override
    public Void execute(VerifyResetCodeInput input) {
        String codeHash = tokenHashService.hash(input.code());

        User user = userRepository.findByEmail(input.email())
                .orElseThrow(InvalidTokenException::new);

        PasswordResetCode resetCode =
                codeRepository.findActiveByUserId(user.getUserId())
                        .orElseThrow(InvalidTokenException::new);

        try {
            resetCode.validate(codeHash);

        } catch (InvalidTokenException e) {
            codeRepository.save(resetCode);
            throw e;
        }

        codeRepository.save(resetCode);

        return null;
    }
}
