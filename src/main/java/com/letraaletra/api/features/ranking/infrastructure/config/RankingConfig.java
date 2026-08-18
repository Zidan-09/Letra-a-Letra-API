package com.letraaletra.api.features.ranking.infrastructure.config;

import com.letraaletra.api.features.ranking.application.usecase.ExitRankingQueueUseCase;
import com.letraaletra.api.features.ranking.application.usecase.JoinRankingUseCase;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingConfig {
    @Bean
    public JoinRankingUseCase joinRankingUseCase(
            RankingRepository rankingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        return new JoinRankingUseCase(
                rankingRepository,
                userRepository,
                queueChecker
        );
    }

    @Bean
    public ExitRankingQueueUseCase exitRankingQueueUseCase(
            RankingRepository rankingRepository,
            UserRepository userRepository,
            QueueChecker queueChecker
    ) {
        return new ExitRankingQueueUseCase(
                rankingRepository,
                userRepository,
                queueChecker
        );
    }
}
