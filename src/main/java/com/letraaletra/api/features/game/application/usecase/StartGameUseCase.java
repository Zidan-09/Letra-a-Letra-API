package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.actor.command.StartCustomGameActorCommand;
import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.service.GameTimeoutManager;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.Board;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StartGameUseCase implements UseCase<StartGameInput, StartGameOutput> {
    private final GameRepository gameRepository;
    private final GameTimeoutManager gameTimeoutManager;
    private final SelectThemeService themeService;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;

    public StartGameUseCase(
            GameRepository gameRepository,
            GameTimeoutManager gameTimeoutManager,
            SelectThemeService themeService,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager
    ) {
        this.gameRepository = gameRepository;
        this.gameTimeoutManager = gameTimeoutManager;
        this.themeService = themeService;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
    }

    @Override
    @Transactional
    public StartGameOutput execute(StartGameInput input) {
        List<String> words = themeService.select(input.settings().getThemeId());

        Board board = BoardGenerator.generate(words, input.settings().getGameMode());

        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Game> future = actor.enqueueCommand(new StartCustomGameActorCommand(input.session(), board, gameTimeoutManager, turnTimeoutManager));

        Game game = future.join();

        gameRepository.save(game);

        return new StartGameOutput(game);
    }
}
