package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.actor.PlayerActionActorCommand;
import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.actor.PlayerActionResult;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infrastructure.manager.GameActorManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase {
    private final TokenService tokenService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final GameActorManager gameActorManager;

    public PlayerActionUseCase(
            TokenService tokenService,
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            GameActorManager gameActorManager
    ) {
        this.tokenService = tokenService;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
    }

    public PlayerActionOutput execute(PlayerActionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorManager.getOrCreate(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                command.user(), command.action(), gameTimeoutManager, turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        if (result.gameOverResult().finished()) {
            gameActorManager.remove(gameId);
        }

        return buildOutput(result.game(), result.gameOverResult(), result.events());
    }

    private PlayerActionOutput buildOutput(Game game, GameOverResult gameOverResult, List<StateEvent> event) {
        return new PlayerActionOutput(
                game,
                event,
                gameOverResult.finished() ? Optional.of(gameOverResult) : Optional.empty()
        );
    }
}
