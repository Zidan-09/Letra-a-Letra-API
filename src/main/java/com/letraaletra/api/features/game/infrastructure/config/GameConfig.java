package com.letraaletra.api.features.game.infrastructure.config;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.output.GetActiveGamesOutput;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.port.GameOverFinalizer;
import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.shared.infrastructure.websocket.WsConnectionRegistry;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.usecase.*;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.game.infrastructure.websocket.assembler.GameResponseAssemblerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {
    @Bean
    public UseCase<CreateGameInput, CreateGameOutput> createGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            RoomTimeoutManager roomTimeoutManager,
            RoomCodeService roomCodeService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new CreateGameUseCase(
                        userRepository,
                        gameRepository,
                        actorManager,
                        roomTimeoutManager,
                        roomCodeService
                ),
                transactions
        );
    }

    @Bean
    public UseCase<FindByCodeInput, FindByCodeOutput> findByCodeUseCase(
            GameQueryService gameQueryService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new FindByCodeUseCase(gameQueryService),
                transactions
        );
    }

    @Bean
    public UseCase<GetPublicGamesInput, GetPublicGamesOutput> getPublicGamesUseCase(
            GameQueryService gameQueryService,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetPublicGamesUseCase(gameQueryService),
                transactions
        );
    }

    @Bean
    public UseCase<JoinGameInput, JoinGameOutput> joinGameUseCase(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new JoinGameUseCase(userRepository, actorManager),
                transactions
        );
    }

    @Bean
    public UseCase<LeftGameInput, LeftGameOutput> leftGameUseCase(
            GameActorManager gameActorManager,
            UserRepository userRepository,
            GameOverFinalizer gameOverFinalizer,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new LeftGameUseCase(
                        gameActorManager,
                        userRepository,
                        gameOverFinalizer
                ),
                transactions
        );
    }

    @Bean
    public UseCase<StartGameInput, StartGameOutput> startGameUseCase(
            GameRepository gameRepository,
             RoomTimeoutManager roomTimeoutManager,
             SelectThemeService themeService,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager,
             BusinessAuditRecorder auditRecorder,
             TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new StartGameUseCase(
                        gameRepository,
                        roomTimeoutManager,
                        themeService,
                        turnTimeoutManager,
                        gameActorManager,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public GameResponseAssemblerService gameResponseAssemblerService(
            UserRepository userRepository,
            WsConnectionRegistry connectionRegistry
    ) {
        return new GameResponseAssemblerService(
                userRepository,
                connectionRegistry
        );
    }

    @Bean
    public UseCase<GetGamesInput, GetGamesOutput> getGamesUseCase(
            GameRepository gameRepository,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetGamesUseCase(
                        gameRepository,
                        adminChecker
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetActiveGamesInput, GetActiveGamesOutput> getActiveGamesUseCase(
            GameQueryService gameQueryService,
            AdminChecker adminChecker,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetActiveGamesUseCase(
                        gameQueryService,
                        adminChecker
                ),
                transactions
        );
    }
}
