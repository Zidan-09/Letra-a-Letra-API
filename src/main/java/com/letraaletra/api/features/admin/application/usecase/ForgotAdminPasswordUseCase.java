package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.ForgotAdminPasswordInput;
import com.letraaletra.api.features.admin.application.port.PasswordResetTokenEmailService;
import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.service.TokenHashService;

import java.util.UUID;

public class ForgotAdminPasswordUseCase implements UseCase<ForgotAdminPasswordInput, Void> {
    private final AdminRepository adminRepository;
    private final TokenHashService tokenHashService;
    private final AdminResetTokenRepository tokenRepository;
    private final PasswordResetTokenEmailService emailService;

    public ForgotAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository,
            PasswordResetTokenEmailService emailService
    ) {
        this.adminRepository = adminRepository;
        this.tokenHashService = tokenHashService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    public Void execute(ForgotAdminPasswordInput input) {
        Admin admin = adminRepository.findByEmail(input.email()).orElse(null);

        if (admin == null) return null;

        tokenRepository.invalidateAllByAdminId(admin.getId());

        String token = UUID.randomUUID().toString();

        String tokenHashed = tokenHashService.hash(token);

        AdminPasswordResetToken adminPasswordResetToken = AdminPasswordResetToken.create(
                admin.getId(),
                tokenHashed
        );

        tokenRepository.save(adminPasswordResetToken);

        emailService.send(
                admin.getEmail(),
                admin.getName(),
                token
        );

        return null;
    }
}
