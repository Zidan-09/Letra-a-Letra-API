package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.features.user.domain.exception.SamePasswordException;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.springframework.transaction.annotation.Transactional;

public class ResetAdminPasswordUseCase implements UseCase<ResetAdminPasswordInput, Void> {
    private final AdminRepository adminRepository;
    private final TokenHashService tokenHashService;
    private final PasswordService passwordService;
    private final AdminResetTokenRepository tokenRepository;

    public ResetAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            AdminResetTokenRepository tokenRepository
    ) {
        this.adminRepository = adminRepository;
        this.tokenHashService = tokenHashService;
        this.passwordService = passwordService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public Void execute(ResetAdminPasswordInput input) {
        String tokenHash = tokenHashService.hash(input.token());

        AdminPasswordResetToken resetToken = tokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        resetToken.validate(tokenHash);

        Admin admin = adminRepository.find(resetToken.getAdminId())
                .orElseThrow(InvalidTokenException::new);

        if (passwordService.matches(input.newPassword(), admin.getPasswordHash())) {
            throw new SamePasswordException();
        }

        resetToken.markAsUsed();

        admin.changePassword(passwordService.hash(input.newPassword()));

        adminRepository.save(admin);
        tokenRepository.save(resetToken);

        return null;
    }
}
