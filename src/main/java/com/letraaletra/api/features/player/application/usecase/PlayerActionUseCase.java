package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.PlayerActionActorCommand;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.game.domain.actor.output.PlayerActionResult;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.application.service.GameOverHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase implements UseCase<PlayerActionInput, PlayerActionOutput> {
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    public PlayerActionUseCase(
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler
    ) {
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
    }

    public PlayerActionOutput execute(PlayerActionInput input) {
        UUID gameId = UUID.fromString(input.gameId());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                input.user(), input.action(), gameTimeoutManager, turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        result.gameOver().ifPresent(over -> gameOverHandler.handle(result.game(), over));

        return buildOutput(result);
    }

    private PlayerActionOutput buildOutput(PlayerActionResult result) {
        return new PlayerActionOutput(
                result.game(),
                result.events(),
                result.gameOver()
        );
    }
}
