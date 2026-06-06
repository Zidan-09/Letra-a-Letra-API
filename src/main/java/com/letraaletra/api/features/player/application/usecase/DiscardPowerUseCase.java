package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.DiscardPowerActorCommand;
import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;

import java.util.concurrent.CompletableFuture;

public class DiscardPowerUseCase implements UseCase<DiscardPowerInput, DiscardPowerOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> gameActorManager;

    public DiscardPowerUseCase(TokenService tokenService, ActorManager<Game> gameActorManager) {
        this.tokenService = tokenService;
        this.gameActorManager = gameActorManager;
    }

    public DiscardPowerOutput execute(DiscardPowerInput command) {
        String gameId = tokenService.getTokenContent(command.tokenGameId());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(
                new DiscardPowerActorCommand(command.userId(), command.powerId())
        );

        Game game = future.join();

        return buildOutput(game);
    }

    private DiscardPowerOutput buildOutput(Game game) {
        return new DiscardPowerOutput(game);
    }
}
