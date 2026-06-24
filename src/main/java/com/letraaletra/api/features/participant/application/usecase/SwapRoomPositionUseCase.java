package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.SwapPositionActorCommand;
import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SwapRoomPositionUseCase implements UseCase<SwapPositionInput, SwapPositionOutput> {
    private final ActorManager<Game> gameActorManager;
    private final TokenService tokenService;

    public SwapRoomPositionUseCase(ActorManager<Game> gameActorManager, TokenService tokenService) {
        this.gameActorManager = gameActorManager;
        this.tokenService = tokenService;
    }

    public SwapPositionOutput execute(SwapPositionInput input) {
        UUID gameId = tokenService.getTokenContent(input.token());

        Actor actor = gameActorManager.get(gameId.toString());
        validateActor(actor);

        CompletableFuture<Game> future = actor.enqueueCommand(new SwapPositionActorCommand(input.user(), input.position()));
        Game game = future.join();

        return buildReturn(game, input.token());
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
