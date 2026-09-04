package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.DiscardPowerActorCommand;
import com.letraaletra.api.features.game.domain.actor.result.DiscardPowerResult;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class DiscardPowerUseCase implements UseCase<DiscardPowerInput, DiscardPowerOutput> {
    private final ActorManager<Game> gameActorManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public DiscardPowerUseCase(ActorManager<Game> gameActorManager, TurnTimeoutManager turnTimeoutManager) {
        this.gameActorManager = gameActorManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    public DiscardPowerUseCase(ActorManager<Game> gameActorManager) {
        this(gameActorManager, null);
    }

    @Override
    public DiscardPowerOutput execute(DiscardPowerInput input) {
        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<DiscardPowerResult> future = actor.enqueueCommand(
                new DiscardPowerActorCommand(input.userId(), input.powerId(), turnTimeoutManager)
        );

        DiscardPowerResult result = future.join();

        return new DiscardPowerOutput(result.game(), result.events());
    }
}
