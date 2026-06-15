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
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.shared.domain.security.TokenService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerActionUseCase implements UseCase<PlayerActionInput, PlayerActionOutput> {
    private final TokenService tokenService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final GameOverHandler gameOverHandler;

    public PlayerActionUseCase(
            TokenService tokenService,
            GameTimeoutManager gameTimeoutManager,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            GameOverHandler gameOverHandler
    ) {
        this.tokenService = tokenService;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.gameOverHandler = gameOverHandler;
    }

    public PlayerActionOutput execute(PlayerActionInput input) {
        String gameId = tokenService.getTokenContent(input.token());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<PlayerActionResult> future = actor.enqueueCommand(new PlayerActionActorCommand(
                input.user(), input.action(), gameTimeoutManager, turnTimeoutManager
        ));

        PlayerActionResult result = future.join();

        gameOverHandler.handle(result.game(), result.gameOverResult());

        return buildOutput(result.game(), result.gameOverResult(), result.events());
    }

    private PlayerActionOutput buildOutput(Game game, GameOverResult gameOverResult, List<Event> events) {
        return new PlayerActionOutput(
                game,
                events,
                gameOverResult.finished() ? Optional.of(gameOverResult) : Optional.empty()
        );
    }
}
