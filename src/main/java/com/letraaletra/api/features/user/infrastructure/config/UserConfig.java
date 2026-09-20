package com.letraaletra.api.features.user.infrastructure.config;

import com.letraaletra.api.features.user.application.port.GoogleTokenService;
import com.letraaletra.api.features.user.application.port.NicknameService;
import com.letraaletra.api.features.user.application.port.RefreshTokenGenerator;
import com.letraaletra.api.features.user.application.port.ResetCodeService;
import com.letraaletra.api.features.user.application.port.PasswordResetCodeEmailService;
import com.letraaletra.api.features.user.application.input.AuthInput;
import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.input.ForgotPasswordInput;
import com.letraaletra.api.features.user.application.input.GetMyProfileInput;
import com.letraaletra.api.features.user.application.input.GetMyTransactionsInput;
import com.letraaletra.api.features.user.application.input.GetUsersInput;
import com.letraaletra.api.features.user.application.input.GrantUserRewardInput;
import com.letraaletra.api.features.user.application.input.IssueSessionInput;
import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.input.ResetPasswordInput;
import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.application.input.RevokeUserWalletInput;
import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.input.UnbanUserInput;
import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.application.output.GetMyProfileOutput;
import com.letraaletra.api.features.user.application.output.GetMyTransactionsOutput;
import com.letraaletra.api.features.user.application.output.GetUsersOutput;
import com.letraaletra.api.features.user.application.output.IssueSessionOutput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.transaction.application.input.GetTransactionsInput;
import com.letraaletra.api.features.transaction.application.output.GetTransactionsOutput;
import com.letraaletra.api.features.user.application.usecase.GetUsersUseCase;
import com.letraaletra.api.features.transaction.application.usecase.GetTransactionsUseCase;
import com.letraaletra.api.features.user.application.usecase.*;
import com.letraaletra.api.features.user.domain.ban.repository.BanHistoryRepository;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.reset.repository.ResetCodeRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidResetCodeException;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.reward.application.port.RewardFactory;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import com.letraaletra.api.shared.domain.security.PasswordService;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class UserConfig {
    @Bean
    public UseCase<CreateUserInput, CreateUserOutput> createUserUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            NicknameService nicknameService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateUserUseCase(
                        userRepository,
                        passwordService,
                        nicknameService
                ),
                transactions
        );
    }

    @Bean
    public UseCase<AuthInput, SignInOutput> authUseCase(
            TokenService tokenService,
            NicknameService nicknameService,
            UserRepository userRepository,
            GoogleTokenService googleTokenService,
            UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GoogleAuthUseCase(
                        tokenService,
                        nicknameService,
                        userRepository,
                        googleTokenService,
                        issueSessionUseCase
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ChangeNicknameInput, ChangeNicknameOutput> setNicknameUseCase(
            UserRepository userRepository,
            com.letraaletra.api.features.items.domain.repository.ItemLookup itemLookup,
            com.letraaletra.api.features.inventory.domain.repository.InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ChangeNicknameUseCase(
                        userRepository,
                        itemLookup,
                        inventoryRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<SignInInput, SignInOutput> signInUseCase(
            UserRepository userRepository,
            PasswordService passwordService,
            TokenService tokenService,
            UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new AuthUserUseCase(
                        userRepository,
                        passwordService,
                        tokenService,
                        issueSessionUseCase
                ),
                transactions
        );
    }

    @Bean
    public UseCase<IssueSessionInput, IssueSessionOutput> issueSessionUseCase(
            UserSessionRepository sessionRepository,
            RefreshTokenGenerator refreshTokenGenerator,
            TokenHashService tokenHashService,
            @Value("${api.security.refresh.expiration}") long refreshExpirationMillis,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new IssueSessionUseCase(
                        sessionRepository,
                        refreshTokenGenerator,
                        tokenHashService,
                        refreshExpirationMillis
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RefreshSessionInput, RefreshSessionOutput> refreshSessionUseCase(
            UserSessionRepository sessionRepository,
            UserRepository userRepository,
            TokenHashService tokenHashService,
            RefreshTokenGenerator refreshTokenGenerator,
            TokenService tokenService,
            @Value("${api.security.refresh.expiration}") long refreshExpirationMillis,
            @Value("${api.security.refresh.recovery-window}") long recoveryWindowMillis,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RefreshSessionUseCase(
                        sessionRepository,
                        userRepository,
                        tokenHashService,
                        refreshTokenGenerator,
                        tokenService,
                        refreshExpirationMillis,
                        recoveryWindowMillis
                ),
                transactions,
                Set.of(InvalidTokenException.class, UserBannedFromGameException.class)
        );
    }

    @Bean
    public UseCase<RevokeSessionInput, Void> revokeSessionUseCase(
            UserSessionRepository sessionRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RevokeSessionUseCase(
                        sessionRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetMyProfileInput, GetMyProfileOutput> getMyProfileUseCase(
            UserRepository userRepository,
            com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetMyProfileUseCase(
                        userRepository,
                        equippedItemsProvider
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetTransactionsInput, GetTransactionsOutput> getTransactionsUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetTransactionsUseCase(
                        transactionRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetUsersInput, GetUsersOutput> getUsersUseCase(
            UserRepository userRepository,
            com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetUsersUseCase(
                        userRepository,
                        equippedItemsProvider
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindUserByUsernameInput, FindUserByUsernameOutput> findUserByUsernameUseCase(
            UserRepository userRepository,
            com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindUserByUsernameUseCase(
                        userRepository,
                        equippedItemsProvider
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetMyTransactionsInput, GetMyTransactionsOutput> getMyTransactionsUseCase(
            TransactionRepository transactionRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetMyTransactionsUseCase(
                        transactionRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ForgotPasswordInput, Void> forgotPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            ResetCodeRepository resetCodeRepository,
            ResetCodeService resetCodeService,
            PasswordResetCodeEmailService emailService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ForgotPasswordUseCase(
                        userRepository,
                        tokenHashService,
                        resetCodeRepository,
                        resetCodeService,
                        emailService
                ),
                transactions
        );
    }

    @Bean
    public UseCase<VerifyResetCodeInput, Void> verifyResetCodeUseCase(
            UserRepository userRepository,
            ResetCodeRepository resetCodeRepository,
            TokenHashService tokenHashService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new VerifyResetCodeUseCase(
                        userRepository,
                        resetCodeRepository,
                        tokenHashService
                ),
                transactions,
                Set.of(InvalidTokenException.class, InvalidResetCodeException.class)
        );
    }

    @Bean
    public UseCase<ResetPasswordInput, Void> resetPasswordUseCase(
            UserRepository userRepository,
            TokenHashService tokenHashService,
            PasswordService passwordService,
            ResetCodeRepository resetCodeRepository,
            UserSessionRepository sessionRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ResetPasswordUseCase(
                        userRepository,
                        tokenHashService,
                        passwordService,
                        resetCodeRepository,
                        sessionRepository
                ),
                transactions,
                Set.of(InvalidTokenException.class, InvalidResetCodeException.class)
        );
    }

    @Bean
    public UseCase<BanUserInput, Void> banUserUseCase(
            UserRepository userRepository,
            BanHistoryRepository banHistoryRepository,
            AdminChecker adminChecker,
            UserSessionRepository sessionRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new BanUserUseCase(
                        userRepository,
                        banHistoryRepository,
                        adminChecker,
                        sessionRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<UnbanUserInput, Void> unbanUserUseCase(
            UserRepository userRepository,
            BanHistoryRepository banHistoryRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new UnbanUserUseCase(
                        userRepository,
                        banHistoryRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GrantUserRewardInput, Void> grantUserRewardUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            RewardFactory rewardFactory,
            com.letraaletra.api.features.items.domain.repository.ItemRepository itemRepository,
            com.letraaletra.api.features.inventory.domain.repository.InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GrantUserRewardUseCase(
                        userRepository,
                        transactionRepository,
                        adminChecker,
                        rewardFactory,
                        itemRepository,
                        inventoryRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RevokeUserWalletInput, Void> revokeUserWalletUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RevokeUserWalletUseCase(
                        userRepository,
                        transactionRepository,
                        adminChecker,
                        auditRecorder
                ),
                transactions
        );
    }
}
