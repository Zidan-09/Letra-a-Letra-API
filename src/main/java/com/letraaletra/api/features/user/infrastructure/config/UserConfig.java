package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.features.user.application.port.ResetCodeService;
import com.letraaletra.api.features.user.application.port.PasswordResetCodeEmailService;
import com.letraaletra.api.features.user.application.usecase.GetUsersUseCase;
import com.letraaletra.api.features.transaction.application.usecase.GetTransactionsUseCase;
import com.letraaletra.api.features.user.application.usecase.*;
import com.letraaletra.api.features.user.domain.repository.InventoryRepository;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.repository.ResetCodeRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            NicknameService nicknameService
    ) {
        return new CreateUserUseCase(
                userRepository,
                passwordService,
                nicknameService
        );
    }

    @Bean
    public GoogleAuthUseCase authUseCase(
            TokenService tokenService,
            NicknameService nicknameService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService
    ) {
        return new GoogleAuthUseCase(
                tokenService,
                nicknameService,
                userRepository,
                googleTokenService
        );
    }

    @Bean
    public ChangeNicknameUseCase setNicknameUseCase(
            UserRepository userRepository
    ) {
        return new ChangeNicknameUseCase(
                userRepository
        );
    }

    @Bean
    public AuthUserUseCase signInUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService
    ) {
        return new AuthUserUseCase(
                userRepository,
                passwordService,
                tokenService
        );
    }

    @Bean
    public GetMyInventoryUseCase getUserInventoryUseCase(
            InventoryRepository inventoryRepository
    ) {
        return new GetMyInventoryUseCase(
                inventoryRepository
        );
    }

    @Bean
    public GetMyProfileUseCase getMyProfileUseCase(
            UserRepository userRepository
    ) {
        return new GetMyProfileUseCase(
                userRepository
        );
    }

    @Bean
    public GetTransactionsUseCase getTransactionsUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        return new GetTransactionsUseCase(
                transactionRepository,
                adminChecker
        );
    }

    @Bean
    public GetUsersUseCase getUsersUseCase(
            UserRepository userRepository,
            AdminChecker adminChecker
    ) {
        return new GetUsersUseCase(
                userRepository,
                adminChecker
        );
    }

    @Bean
    public FindUserByUsernameUseCase findUserByUsernameUseCase(
            UserRepository userRepository
    ) {
        return new FindUserByUsernameUseCase(
                userRepository
        );
    }

    @Bean
    public GetMyTransactionsUseCase getMyTransactionsUseCase(
            TransactionRepository transactionRepository
    ) {
        return new GetMyTransactionsUseCase(
                transactionRepository
        );
    }

    @Bean
    public ForgotPasswordUseCase forgotPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            ResetCodeRepository resetCodeRepository,
            ResetCodeService resetCodeService,
            PasswordResetCodeEmailService emailService
    ) {
        return new ForgotPasswordUseCase(
                userRepository,
                tokenHashService,
                resetCodeRepository,
                resetCodeService,
                emailService
        );
    }

    @Bean
    public VerifyResetCodeUseCase verifyResetCodeUseCase(
            ResetCodeRepository resetCodeRepository,
            TokenHashService tokenHashService
    ) {
        return new VerifyResetCodeUseCase(
                resetCodeRepository,
                tokenHashService
        );
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            ResetCodeRepository resetCodeRepository
    ) {
        return new ResetPasswordUseCase(
                userRepository,
                tokenHashService,
                passwordService,
                resetCodeRepository
        );
    }
}
