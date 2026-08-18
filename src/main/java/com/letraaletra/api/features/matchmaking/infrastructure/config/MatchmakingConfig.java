package com.letraaletra.api.features.matchmaking.infrastructure.config;

import com.letraaletra.api.features.matchmaking.application.usecase.ExitMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.application.usecase.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchmakingConfig {
    @Bean
    public JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        return new JoinMatchmakingQueueUseCase(
                matchmakingRepository,
                userRepository,
                queueChecker
        );
    }

    @Bean
    public ExitMatchmakingQueueUseCase exitMatchmakingQueueUseCase(
            MatchmakingRepository matchmakingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        return new ExitMatchmakingQueueUseCase(
                matchmakingRepository,
                userRepository,
                queueChecker
        );
    }
}
