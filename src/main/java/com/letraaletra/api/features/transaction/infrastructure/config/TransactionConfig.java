package com.letraaletra.api.features.transaction.infrastructure.config;

import com.letraaletra.api.features.transaction.application.input.FindTransactionInput;
import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserInput;
import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserUsernameInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionOutput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserOutput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserUsernameOutput;
import com.letraaletra.api.features.transaction.application.usecase.FindTransactionByUserUseCase;
import com.letraaletra.api.features.transaction.application.usecase.FindTransactionUseCase;
import com.letraaletra.api.features.transaction.application.usecase.FindTransactionsByUserUsernameUseCase;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {
    @Bean
    public UseCase<FindTransactionInput, FindTransactionOutput> findTransactionUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindTransactionUseCase(
                        transactionRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindTransactionsByUserInput, FindTransactionsByUserOutput> findTransactionByUserUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindTransactionByUserUseCase(
                        transactionRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindTransactionsByUserUsernameInput, FindTransactionsByUserUsernameOutput> findTransactionsByUserNicknameUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindTransactionsByUserUsernameUseCase(
                        userRepository,
                        transactionRepository,
                        adminChecker
                ),
                transactions
        );
    }
}
