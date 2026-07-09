package com.letraaletra.api.features.matchmaking.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.service.PickRandomThemeWordsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.factory.DefaultGameFactory;
import com.letraaletra.api.features.game.domain.factory.DefaultGameStateFactory;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.matchmaking.application.service.MatchmakingAssembler;
import com.letraaletra.api.features.matchmaking.application.usecase.ExitMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.application.usecase.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.port.QueueChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchmakingConfig {
    @Bean
    public MatchmakingAssembler matchmakingGameFactory(
            DefaultGameFactory gameFactory,
            DefaultGameStateFactory stateFactory,
            PickRandomThemeWordsService wordsService,
            GenerateRoomCode generateRoomCode,
            GameQueryService queryService,
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager
    ) {
        return new MatchmakingAssembler(
                gameFactory,
                stateFactory,
                wordsService,
                generateRoomCode,
                queryService,
                userRepository,
                gameRepository,
                actorManager
        );
    }

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
