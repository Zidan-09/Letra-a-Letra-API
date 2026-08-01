package com.letraaletra.api.features.game.infrastructure.config;

import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.application.service.*;
import com.letraaletra.api.features.game.domain.board.cell.service.CellFactory;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.ranking.application.service.UpdateRankingPointsService;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.usecase.*;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.game.infrastructure.concurrency.GameActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.infrastructure.websocket.broadcast.GameResponseAssemblerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            GameRepository gameRepository,
             ThemeRepository themeRepository,
             GameTimeoutManager gameTimeoutManager,
             PickRandomThemeWordsService pickRandomThemeWordsService,
             BoardGenerator boardGenerator,
             TurnTimeoutManager turnTimeoutManager,
             GameActorManager gameActorManager
    ) {
        return new StartGameUseCase(
                gameRepository,
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
            GameOverHandler gameOverHandler
    ) {
        return new ExpireTurnService(gameActorManager, gameOverHandler);
    }

    @Bean
    public PickRandomThemeWordsService pickRandomThemeWordsUseCase(ThemeRepository themeRepository) {
        return new PickRandomThemeWordsService(themeRepository);
    }

    @Bean
    public GameOverHandler gameOverHandler(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> actorManager,
            GameTimeoutManager gameTimeoutManager,
            UpdateStatsService updateStatsService,
            AuditService auditService
    ) {
        return new GameOverHandler(
                gameRepository,
                userRepository,
                actorManager,
                gameTimeoutManager,
                updateStatsService,
                auditService
        );
    }

    @Bean
    public UpdateStatsService updateStatsService(
            LevelRepository levelRepository,
            TransactionRepository walletTransactionRepository
    ) {
        return new UpdateStatsService(
                levelRepository,
                walletTransactionRepository
        );
    }

    @Bean
    public BoardGenerator boardGenerator(CellFactory cellFactory) {
        return new BoardGenerator(cellFactory);
    }

    @Bean
    public CellFactory cellFactory() {
        return new CellFactory();
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
