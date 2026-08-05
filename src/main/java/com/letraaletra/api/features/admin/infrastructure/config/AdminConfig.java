package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.application.port.AdminInvitationEmailService;
import com.letraaletra.api.features.admin.application.port.PasswordResetTokenEmailService;
import com.letraaletra.api.features.admin.application.usecase.*;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    @Bean
    public RegisterAdminUseCase registerAdminUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminTokenRepository adminTokenRepository,
            AdminInvitationEmailService emailService,
            AdminChecker adminChecker
    ) {
        return new RegisterAdminUseCase(
                adminRepository,
                tokenHashService,
                adminTokenRepository,
                emailService,
                adminChecker
        );
    }

    @Bean
    public AuthAdminUseCase authAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        return new AuthAdminUseCase(
                adminRepository,
                passwordService,
                tokenService
        );
    }

    @Bean
    public GetMyAdminProfileUseCase getMyAdminProfileUseCase(
            AdminRepository adminRepository
    ) {
        return new GetMyAdminProfileUseCase(
                adminRepository
        );
    }

    @Bean
    public GetAdminsUseCase getAdminsUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        return new GetAdminsUseCase(
                adminRepository,
                adminChecker
        );
    }

    @Bean
    public FindAdminByEmailUseCase findAdminByUsernameUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        return new FindAdminByEmailUseCase(
                adminRepository,
                adminChecker
        );
    }

    @Bean
    public UpdateAdminUseCase updateAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        return new UpdateAdminUseCase(
                adminRepository,
                adminChecker
        );
    }

    @Bean
    public DeleteAdminUseCase deleteAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker
    ) {
        return new DeleteAdminUseCase(
                adminRepository,
                adminChecker
        );
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(
            TokenHashService tokenHashService,
            AdminRepository adminRepository,
            AdminTokenRepository adminTokenRepository,
            PasswordService passwordService
    ) {
        return new ActivateAccountUseCase(
                tokenHashService,
                adminRepository,
                adminTokenRepository,
                passwordService
        );
    }

    @Bean
    public ForgotAdminPasswordUseCase forgotAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository,
            PasswordResetTokenEmailService emailService
    ) {
        return new ForgotAdminPasswordUseCase(
                adminRepository,
                tokenHashService,
                tokenRepository,
                emailService
        );

    }

    @Bean
    public VerifyResetTokenUseCase verifyResetTokenUseCase(
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository
    ) {
        return new VerifyResetTokenUseCase(
                tokenHashService,
                tokenRepository
        );
    }

    @Bean
    public ResetAdminPasswordUseCase resetAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            AdminResetTokenRepository tokenRepository
    ) {
        return new ResetAdminPasswordUseCase(
                adminRepository,
                tokenHashService,
                passwordService,
                tokenRepository
        );
    }
}
