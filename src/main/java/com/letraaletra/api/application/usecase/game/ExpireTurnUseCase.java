package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.command.actor.ExpireTurnActorCommand;
import com.letraaletra.api.application.command.game.ExpireTurnCommand;
import com.letraaletra.api.application.output.actor.ExpireTurnResult;
import com.letraaletra.api.application.output.game.ExpireTurnOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.infrastructure.manager.GameActorManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExpireTurnUseCase {
    private final GameActorManager gameActorManager;

    public ExpireTurnUseCase(GameActorManager gameActorManager) {
        this.gameActorManager = gameActorManager;
    }

    public Optional<ExpireTurnOutput> execute(ExpireTurnCommand command) {
        Actor actor = gameActorManager.getOrCreate(command.gameId());

        CompletableFuture<Optional<ExpireTurnResult>> future = actor.enqueueCommand(new ExpireTurnActorCommand(command.version()));
        Optional<ExpireTurnResult> result = future.join();

        return result.flatMap(expireTurnResult -> buildOutput(expireTurnResult.whoPassed(), expireTurnResult.game()));

    }

    private Optional<ExpireTurnOutput> buildOutput(String whoPassed, Game game) {
        return Optional.of(
                new ExpireTurnOutput("TURN_EXPIRED", whoPassed, game.getGameState().currentPlayerTurn(), game)
        );
    }
}
