package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.actor.DiscardPowerActorCommand;
import com.letraaletra.api.application.command.player.DiscardPowerCommand;
import com.letraaletra.api.application.output.player.DiscardPowerOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;

import java.util.concurrent.CompletableFuture;

public class DiscardPowerUseCase {
    private final TokenService tokenService;
    private final GameActorManager gameActorManager;

    public DiscardPowerUseCase(TokenService tokenService, GameActorManager gameActorManager) {
        this.tokenService = tokenService;
        this.gameActorManager = gameActorManager;
    }

    public DiscardPowerOutput execute(DiscardPowerCommand command) {
        String gameId = tokenService.getTokenContent(command.tokenGameId());

        Actor actor = gameActorManager.getOrCreate(gameId);

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
