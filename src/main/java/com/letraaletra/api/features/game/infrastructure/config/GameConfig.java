package com.letraaletra.api.features.game.infrastructure.config;

import com.letraaletra.api.features.game.application.service.*;
import com.letraaletra.api.features.game.domain.board.cell.service.CellFactory;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.ranking.application.service.UpdateRankingPointsService;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.usecase.*;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.factory.DefaultGameFactory;
import com.letraaletra.api.features.game.domain.factory.DefaultGameStateFactory;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.infrastructure.websocket.broadcast.GameResponseAssemblerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class GameConfig {
    @Bean
    public CloseRoomDueToTimeoutService closeRoomDueToTimeoutUseCase(
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameRepository gameRepository
    ) {
        return new CloseRoomDueToTimeoutService(
                userRepository,
                actorManager,
                gameRepository
        );
    }

    @Bean
    public CreateGameUseCase createGameUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            GameQueryService gameQueryService,
            GenerateRoomCode generateRoomCode
    ) {
        return new CreateGameUseCase(
                userRepository,
                gameRepository,
                actorManager,
                gameTimeoutManager,
                gameQueryService,
                generateRoomCode
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
            GameRepository gameRepository
    ) {
        return new LeftGameUseCase(
                gameActorManager,
                userRepository,
                gameRepository
        );
    }

    @Bean
    public StartGameUseCase startGameUseCase(
             GameStateFactory gameStateFactory,
             ThemeRepository themeRepository,
             GameTimeoutManager gameTimeoutManager,
             PickRandomThemeWordsService pickRandomThemeWordsService,
             BoardGenerator boardGenerator,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager
    ) {
        return new StartGameUseCase(
                gameStateFactory,
                themeRepository,
                gameTimeoutManager,
                pickRandomThemeWordsService,
                boardGenerator,
                turnTimeoutManager,
                gameActorManager
        );
    }

    @Bean
    public ExpireTurnService expireTurnUseCase(
            GameActorManager gameActorManager,
            GameOverHandler gameOverHandler,
            UserRepository userRepository
    ) {
        return new ExpireTurnService(gameActorManager, gameOverHandler, userRepository);
    }

    @Bean
    public PickRandomThemeWordsService pickRandomThemeWordsUseCase(ThemeRepository themeRepository) {
        return new PickRandomThemeWordsService(themeRepository, new Random());
    }

    @Bean
    public GameOverHandler gameOverHandler(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            UpdateStatsService updateStatsService
    ) {
        return new GameOverHandler(
                gameRepository,
                userRepository,
                actorManager,
                gameTimeoutManager,
                updateStatsService
        );
    }

    @Bean
    public UpdateStatsService updateStatsService(
            UserRepository userRepository,
            LevelRepository levelRepository
    ) {
        return new UpdateStatsService(
                userRepository,
                levelRepository
        );
    }

    @Bean
    public BoardGenerator boardGenerator(CellFactory cellFactory) {
        return new BoardGenerator(cellFactory);
    }

    @Bean
    public CellFactory cellFactory() {
        return new CellFactory(new Random());
    }

    @Bean
    public GameStateFactory gameStateGenerator() {
        return new GameStateFactory();
    }

    @Bean
    public GenerateRoomCode generateRoomCode() {
        return new GenerateRoomCode();
    }

    @Bean
    public DefaultGameStateFactory defaultGameStateGenerator(GameStateFactory gameStateFactory, BoardGenerator boardGenerator) {
        return new DefaultGameStateFactory(gameStateFactory, boardGenerator);
    }

    @Bean
    public DefaultGameFactory defaultGameGenerator() {
        return new DefaultGameFactory();
    }

    @Bean
    public GameResponseAssemblerService gameResponseAssemblerService(
            UserRepository userRepository,
            UpdateRankingPointsService rankingPointsService
    ) {
        return new GameResponseAssemblerService(
                userRepository,
                rankingPointsService
        );
    }
}
