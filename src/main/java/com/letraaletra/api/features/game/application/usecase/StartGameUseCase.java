package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.StartGameActorCommand;
import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.PickRandomThemeWordsService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.game.domain.board.theme.Theme;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StartGameUseCase implements UseCase<StartGameInput, StartGameOutput> {
    private final GameStateFactory gameStateFactory;
    private final ThemeRepository themeRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final PickRandomThemeWordsService pickRandomThemeWordsService;
    private final BoardGenerator boardGenerator;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;

    public StartGameUseCase(
            GameStateFactory gameStateFactory,
            ThemeRepository themeRepository,
            GameTimeoutManager gameTimeoutManager,
            PickRandomThemeWordsService pickRandomThemeWordsService,
            BoardGenerator boardGenerator,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager
    ) {
        this.gameStateFactory = gameStateFactory;
        this.themeRepository = themeRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.pickRandomThemeWordsService = pickRandomThemeWordsService;
        this.boardGenerator = boardGenerator;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
    }

    public StartGameOutput execute(StartGameInput input) {
        UUID gameId = input.gameId();

        Theme theme = themeRepository.findById(input.settings().getThemeId());

        List<String> words = (theme != null)
                ? theme.pickRandomWords(5, new Random())
                : pickRandomThemeWordsService.execute();

        Board board = boardGenerator.generate(words, input.settings().getGameMode());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new StartGameActorCommand(input.session(), board, gameStateFactory, gameTimeoutManager, turnTimeoutManager));

        Game game = future.join();

        return buildOutput(game, gameId);
    }

    private StartGameOutput buildOutput(Game game, UUID id) {
        return new StartGameOutput(
                id,
                game
        );
    }
}
