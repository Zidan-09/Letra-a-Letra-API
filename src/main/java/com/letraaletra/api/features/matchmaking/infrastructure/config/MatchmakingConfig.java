package com.letraaletra.api.features.matchmaking.infrastructure.config;

import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;
import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.application.usecase.ExitMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.application.usecase.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchmakingConfig {
    @Bean
    public UseCase<JoinMatchmakingInput, Void> joinMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new JoinMatchmakingQueueUseCase(
                        queueRepository,
                        userRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ExitMatchmakingQueueInput, Void> exitMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ExitMatchmakingQueueUseCase(
                        queueRepository,
                        userRepository
                ),
                transactions
        );
    }
}
