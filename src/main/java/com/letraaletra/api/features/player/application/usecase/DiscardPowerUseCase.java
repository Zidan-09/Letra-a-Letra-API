package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.DiscardPowerActorCommand;
import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DiscardPowerUseCase implements UseCase<DiscardPowerInput, DiscardPowerOutput> {
    private final ActorManager<Game> gameActorManager;

    public DiscardPowerUseCase(ActorManager<Game> gameActorManager) {
        this.gameActorManager = gameActorManager;
    }

    public DiscardPowerOutput execute(DiscardPowerInput input) {
        UUID gameId = UUID.fromString(input.gameId());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(
                new DiscardPowerActorCommand(input.userId(), input.powerId())
        );

        Game game = future.join();

        return buildOutput(game);
    }

    private DiscardPowerOutput buildOutput(Game game) {
        return new DiscardPowerOutput(game);
    }
}
