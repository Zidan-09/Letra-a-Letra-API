package com.letraaletra.api.features.ranking.infrastructure.config;

import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.ranking.application.input.ExitRankingQueueInput;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.application.usecase.ExitRankingQueueUseCase;
import com.letraaletra.api.features.ranking.application.usecase.JoinRankingUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingConfig {
    @Bean
    public UseCase<JoinRankingInput, Void> joinRankingUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new JoinRankingUseCase(
                        queueRepository,
                        userRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ExitRankingQueueInput, Void> exitRankingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ExitRankingQueueUseCase(
                        queueRepository,
                        userRepository
                ),
                transactions
        );
    }
}
