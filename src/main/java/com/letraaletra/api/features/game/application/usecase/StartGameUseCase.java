package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.application.command.actor.StartGameActorCommand;
import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.application.usecase.game.PickRandomThemeWordsUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.service.GameStateGenerator;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.game.domain.board.theme.Theme;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class StartGameUseCase implements UseCase<StartGameInput, StartGameOutput> {
    private final GameStateGenerator gameStateGenerator;
    private final ThemeRepository themeRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase;
    private final BoardGenerator boardGenerator;
    private final TokenService tokenService;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;

    public StartGameUseCase(
            GameStateGenerator gameStateGenerator,
            ThemeRepository themeRepository,
            GameTimeoutManager gameTimeoutManager,
            PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
            BoardGenerator boardGenerator,
            TokenService tokenService,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager
    ) {
        this.gameStateGenerator = gameStateGenerator;
        this.themeRepository = themeRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.pickRandomThemeWordsUseCase = pickRandomThemeWordsUseCase;
        this.boardGenerator = boardGenerator;
        this.tokenService = tokenService;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
    }

    public StartGameOutput execute(StartGameInput command) {
        String gameId = tokenService.getTokenContent(command.token());

        Theme theme = themeRepository.findById(command.settings().getThemeId());

        List<String> words = (theme != null)
                ? theme.pickRandomWords(5, new Random())
                : pickRandomThemeWordsUseCase.execute();

        Board board = boardGenerator.generate(words, command.settings().getGameMode());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new StartGameActorCommand(command.session(), board, gameStateGenerator, gameTimeoutManager, turnTimeoutManager));

        Game game = future.join();

        return buildOutput(game, gameId);
    }

    private StartGameOutput buildOutput(Game game, String id) {
        return new StartGameOutput(
                id,
                game
        );
    }
}
