package com.letraaletra.api.features.transaction.infrastructure.config;

import com.letraaletra.api.features.transaction.application.usecase.FindTransactionByUserUseCase;
import com.letraaletra.api.features.transaction.application.usecase.FindTransactionUseCase;
import com.letraaletra.api.features.transaction.application.usecase.FindTransactionsByUserUsernameUseCase;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {
    @Bean
    public FindTransactionUseCase findTransactionUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        return new FindTransactionUseCase(
                transactionRepository,
                adminChecker
        );
    }

    @Bean
    public FindTransactionByUserUseCase findTransactionByUserUseCase(
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        return new FindTransactionByUserUseCase(
                transactionRepository,
                adminChecker
        );
    }

    @Bean
    public FindTransactionsByUserUsernameUseCase findTransactionsByUserNicknameUseCase(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AdminChecker adminChecker
    ) {
        return new FindTransactionsByUserUsernameUseCase(
                userRepository,
                transactionRepository,
                adminChecker
        );
    }
}
