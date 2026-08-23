package com.letraaletra.api.features.game.infrastructure.config;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
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
            RoomTimeoutManager roomTimeoutManager,
            RoomCodeService roomCodeService
    ) {
        return new CreateGameUseCase(
                userRepository,
                gameRepository,
                actorManager,
                roomTimeoutManager,
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
            RoomTimeoutManager roomTimeoutManager,
            GameOverService gameOverService
    ) {
        return new LeftGameUseCase(
                gameActorManager,
                userRepository,
                gameRepository,
                roomTimeoutManager,
                gameOverService
        );
    }

    @Bean
    public StartGameUseCase startGameUseCase(
            GameRepository gameRepository,
             RoomTimeoutManager roomTimeoutManager,
             SelectThemeService themeService,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager,
             BusinessAuditRecorder auditRecorder
    ) {
        return new StartGameUseCase(
                gameRepository,
                roomTimeoutManager,
                themeService,
                turnTimeoutManager,
                gameActorManager,
                auditRecorder
        );
    }

    @Bean
    public GameResponseAssemblerService gameResponseAssemblerService(
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        return new GameResponseAssemblerService(
                userRepository,
                sessionRepository
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
