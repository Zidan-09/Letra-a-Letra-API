package com.letraaletra.api.features.matchmaking.infrastructure.config;

import com.letraaletra.api.features.matchmaking.application.usecase.ExitMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.application.usecase.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchmakingConfig {
    @Bean
    public JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        return new JoinMatchmakingQueueUseCase(
                queueRepository,
                userRepository
        );
    }

    @Bean
    public ExitMatchmakingQueueUseCase exitMatchmakingQueueUseCase(
            QueueRepository queueRepository,
            UserRepository userRepository
    ) {
        return new ExitMatchmakingQueueUseCase(
                queueRepository,
                userRepository
        );
    }
}
