package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.actor.command.PlayerActionActorCommand;
import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.game.domain.actor.result.PlayerActionResult;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase implements UseCase<PlayerActionInput, PlayerActionOutput> {
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final GameOverService gameOverService;

    public PlayerActionUseCase(
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            GameOverService gameOverService
    ) {
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.gameOverService = gameOverService;
    }

    @Override
    public PlayerActionOutput execute(PlayerActionInput input) {
        UUID gameId = UUID.fromString(input.gameId());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                input.user(), input.action(), turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        HandledGameOver handledGameOver = result.gameOver()
                .map(over -> gameOverService.handle(result.game(), over))
                .orElseGet(HandledGameOver::withoutRanking);

        return buildOutput(result, handledGameOver);
    }

    private PlayerActionOutput buildOutput(PlayerActionResult result, HandledGameOver handledGameOver) {
        return new PlayerActionOutput(
                result.game(),
                result.events(),
                result.gameOver(),
                handledGameOver
        );
    }
}
