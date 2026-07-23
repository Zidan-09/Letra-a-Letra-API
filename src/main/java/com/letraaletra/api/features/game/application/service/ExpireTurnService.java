package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.application.input.ExpireTurnInput;
import com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult;
import com.letraaletra.api.features.game.application.output.ExpireTurnOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnService implements UseCase<ExpireTurnInput, Optional<ExpireTurnOutput>> {
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    public ExpireTurnService(
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler
    ) {
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnInput input) {
        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(input.version())
        );

        Optional<ExpireTurnResult> result = future.join();

        result.ifPresent(r ->
                r.gameOver().ifPresent(gameOver ->
                        gameOverHandler.handle(r.game(), gameOver)
                )
        );

        return result.flatMap(this::buildOutput);
    }

    private Optional<ExpireTurnOutput> buildOutput(ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnOutput(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOver()
                )
        );
    }
}
