package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;

public class VerifyResetTokenUseCase implements UseCase<VerifyResetTokenInput, Void> {
    private final AdminRepository adminRepository;
    private final TokenHashService tokenHashService;
    private final AdminResetTokenRepository tokenRepository;

    public VerifyResetTokenUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository
    ) {
        this.adminRepository = adminRepository;
        this.tokenHashService = tokenHashService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Void execute(VerifyResetTokenInput input) {
        Admin admin = adminRepository.findByEmail(input.email())
                .orElseThrow(InvalidTokenException::new);

        AdminPasswordResetToken resetToken =
                tokenRepository.findLatestByAdminId(admin.getId())
                        .orElseThrow(InvalidTokenException::new);

        resetToken.validate(input.token(), tokenHashService);

        tokenRepository.save(resetToken);

        return null;
    }
}
