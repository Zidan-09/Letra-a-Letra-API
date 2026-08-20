package com.letraaletra.api.features.ranking.infrastructure.config;

import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.ranking.application.usecase.ExitRankingQueueUseCase;
import com.letraaletra.api.features.ranking.application.usecase.JoinRankingUseCase;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingConfig {
    @Bean
    public JoinRankingUseCase joinRankingUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        return new JoinRankingUseCase(
                queueRepository,
                userRepository
        );
    }

    @Bean
    public ExitRankingQueueUseCase exitRankingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        return new ExitRankingQueueUseCase(
                queueRepository,
                userRepository
        );
    }
}
