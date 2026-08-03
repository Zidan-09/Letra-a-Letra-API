package com.letraaletra.api.features.game.infrastructure.config;

import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.application.service.*;
import com.letraaletra.api.features.ranking.application.service.UpdateRankingPointsService;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.infrastructure.websocket.broadcast.GameResponseAssemblerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {
    @Bean
    public CreateGameUseCase createGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            RoomCodeService roomCodeService
    ) {
        return new CreateGameUseCase(
                userRepository,
                gameRepository,
                actorManager,
                gameTimeoutManager,
                roomCodeService
        );
    }

    @Bean
    public FindByCodeUseCase findByCodeUseCase(GameQueryService gameQueryService) {
        return new FindByCodeUseCase( gameQueryService);
    }

    @Bean
    public GetPublicGamesUseCase getPublicGamesUseCase(GameQueryService gameQueryService) {
        return new GetPublicGamesUseCase(gameQueryService);
    }

    @Bean
    public JoinGameUseCase joinGameUseCase(UserRepository userRepository, ActorManager<Game> actorManager) {
        return new JoinGameUseCase(userRepository, actorManager);
    }

    @Bean
    public LeftGameUseCase leftGameUseCase(
            GameActorManager gameActorManager,
            UserRepository userRepository,
            GameRepository gameRepository,
            GameTimeoutManager gameTimeoutManager
    ) {
        return new LeftGameUseCase(
                gameActorManager,
                userRepository,
                gameRepository,
                gameTimeoutManager
        );
    }

    @Bean
    public StartGameUseCase startGameUseCase(
            GameRepository gameRepository,
             GameTimeoutManager gameTimeoutManager,
             SelectThemeService themeService,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager
    ) {
        return new StartGameUseCase(
                gameRepository,
                gameTimeoutManager,
                themeService,
                turnTimeoutManager,
                gameActorManager
        );
    }

    @Bean
    public GameOverHandler gameOverHandler(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            UserStatsService userStatsService
    ) {
        return new GameOverHandler(
                gameRepository,
                userRepository,
                actorManager,
                gameTimeoutManager,
                userStatsService
        );
    }

    @Bean
    public GenerateRoomCode generateRoomCode() {
        return new GenerateRoomCode();
    }

    @Bean
    public GameResponseAssemblerService gameResponseAssemblerService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            UpdateRankingPointsService rankingPointsService
    ) {
        return new GameResponseAssemblerService(
                userRepository,
                sessionRepository,
                rankingPointsService
        );
    }

    @Bean
    public GetGamesUseCase getGamesUseCase(
            GameRepository gameRepository,
            AdminChecker adminChecker
    ) {
        return new GetGamesUseCase(
                gameRepository,
                adminChecker
        );
    }

    @Bean
    public GetActiveGamesUseCase getActiveGamesUseCase(
            GameQueryService gameQueryService,
            AdminChecker adminChecker
    ) {
        return new GetActiveGamesUseCase(
                gameQueryService,
                adminChecker
        );
    }
}
