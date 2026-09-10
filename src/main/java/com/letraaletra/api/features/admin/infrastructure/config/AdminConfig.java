package com.letraaletra.api.features.admin.infrastructure.config;

import com.letraaletra.api.features.admin.application.input.ActivateAccountInput;
import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.input.DeleteAdminInput;
import com.letraaletra.api.features.admin.application.input.FindAdminByEmailInput;
import com.letraaletra.api.features.admin.application.input.ForgotAdminPasswordInput;
import com.letraaletra.api.features.admin.application.input.GetAdminsInput;
import com.letraaletra.api.features.admin.application.input.GetMyAdminProfileInput;
import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.application.input.UpdateAdminInput;
import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.application.output.DeleteAdminOutput;
import com.letraaletra.api.features.admin.application.output.FindAdminByEmailOutput;
import com.letraaletra.api.features.admin.application.output.GetAdminsOutput;
import com.letraaletra.api.features.admin.application.output.GetMyAdminProfileOutput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.application.output.UpdateAdminOutput;
import com.letraaletra.api.features.admin.application.port.AdminInvitationEmailService;
import com.letraaletra.api.features.admin.application.port.PasswordResetTokenEmailService;
import com.letraaletra.api.features.admin.application.usecase.*;
import com.letraaletra.api.features.admin.domain.repository.AdminResetTokenRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.features.admin.domain.repository.AdminTokenRepository;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class AdminConfig {
    @Bean
    public UseCase<RegisterAdminInput, RegisterAdminOutput> registerAdminUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminTokenRepository adminTokenRepository,
            AdminInvitationEmailService emailService,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RegisterAdminUseCase(
                        adminRepository,
                        tokenHashService,
                        adminTokenRepository,
                        emailService,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<AuthAdminInput, AuthAdminOutput> authAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            TokenService tokenService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new AuthAdminUseCase(
                        adminRepository,
                        passwordService,
                        tokenService
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetMyAdminProfileInput, GetMyAdminProfileOutput> getMyAdminProfileUseCase(
            AdminRepository adminRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetMyAdminProfileUseCase(
                        adminRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetAdminsInput, GetAdminsOutput> getAdminsUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetAdminsUseCase(
                        adminRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindAdminByEmailInput, FindAdminByEmailOutput> findAdminByUsernameUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindAdminByEmailUseCase(
                        adminRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UpdateAdminInput, UpdateAdminOutput> updateAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UpdateAdminUseCase(
                        adminRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<DeleteAdminInput, DeleteAdminOutput> deleteAdminUseCase(
            AdminRepository adminRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new DeleteAdminUseCase(
                        adminRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ActivateAccountInput, Void> activateAccountUseCase(
            TokenHashService tokenHashService,
            AdminRepository adminRepository,
            AdminTokenRepository adminTokenRepository,
            PasswordService passwordService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ActivateAccountUseCase(
                        tokenHashService,
                        adminRepository,
                        adminTokenRepository,
                        passwordService
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ForgotAdminPasswordInput, Void> forgotAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository,
            PasswordResetTokenEmailService emailService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ForgotAdminPasswordUseCase(
                        adminRepository,
                        tokenHashService,
                        tokenRepository,
                        emailService
                ),
                transactions
        );

    }

    @Bean
    public UseCase<VerifyResetTokenInput, Void> verifyResetTokenUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            AdminResetTokenRepository tokenRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new VerifyResetTokenUseCase(
                        adminRepository,
                        tokenHashService,
                        tokenRepository
                ),
                transactions,
                Set.of(InvalidTokenException.class)
        );
    }

    @Bean
    public UseCase<ResetAdminPasswordInput, Void> resetAdminPasswordUseCase(
            AdminRepository adminRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            AdminResetTokenRepository tokenRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ResetAdminPasswordUseCase(
                        adminRepository,
                        tokenHashService,
                        passwordService,
                        tokenRepository
                ),
                transactions,
                Set.of(InvalidTokenException.class)
        );
    }
}
