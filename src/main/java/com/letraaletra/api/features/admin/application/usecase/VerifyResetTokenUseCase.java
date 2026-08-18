package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

public class VerifyResetTokenUseCase implements UseCase<VerifyResetTokenInput, Void> {
    private final TokenHashService tokenHashService;
    private final AdminResetTokenRepository tokenRepository;

    public VerifyResetTokenUseCase(
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository
    ) {
        this.tokenHashService = tokenHashService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Void execute(VerifyResetTokenInput input) {
        String tokenHash = tokenHashService.hash(input.token());

        AdminPasswordResetToken resetToken = tokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        resetToken.validate(tokenHash);

        tokenRepository.save(resetToken);

        return null;
    }
}
