package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.application.command.actor.SwapPositionActorCommand;
import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.shared.infrastructure.concurrency.Actor;
import com.letraaletra.api.shared.infrastructure.concurrency.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;

import java.util.concurrent.CompletableFuture;

public class SwapRoomPositionUseCase implements UseCase<SwapPositionInput, SwapPositionOutput> {
    private final ActorManager<Game> gameActorManager;
    private final TokenService tokenService;

    public SwapRoomPositionUseCase(ActorManager<Game> gameActorManager, TokenService tokenService) {
        this.gameActorManager = gameActorManager;
        this.tokenService = tokenService;
    }

    public SwapPositionOutput execute(SwapPositionInput command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorManager.get(gameId);
        validateActor(actor);

        CompletableFuture<Game> future = actor.enqueueCommand(new SwapPositionActorCommand(command.user(), command.position()));
        Game game = future.join();

        return buildReturn(game, command.token());
    }

    private void validateActor(Actor actor) {
        if (actor == null) {
            throw new GameNotFoundException();
        }
    }

    private SwapPositionOutput buildReturn(Game game, String token) {
        return new SwapPositionOutput(
                token,
                game
        );
    }
}
