package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.actor.command.ExpireTurnActorCommand;
import com.letraaletra.api.features.game.domain.ExpireTurnResult;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnService {
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    public ExpireTurnService(
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler
    ) {
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
    }

    public Optional<ExpireTurnResult> execute(UUID gameId, int version) {
        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Optional<com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult>> future = actor.enqueueCommand(
                new ExpireTurnActorCommand(version)
        );

        Optional<com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult> result = future.join();

        result.ifPresent(r ->
                r.gameOver().ifPresent(gameOver ->
                        gameOverHandler.handle(r.game(), gameOver)
                )
        );

        return result.flatMap(this::buildOutput);
    }

    private Optional<ExpireTurnResult> buildOutput(com.letraaletra.api.features.game.domain.actor.output.ExpireTurnResult result) {
        return Optional.of(
                new ExpireTurnResult(
                        "TURN_EXPIRED",
                        result.whoPassed(),
                        result.game().getGameState().currentPlayerTurn(),
                        result.game(),
                        result.gameOver()
                )
        );
    }
}
