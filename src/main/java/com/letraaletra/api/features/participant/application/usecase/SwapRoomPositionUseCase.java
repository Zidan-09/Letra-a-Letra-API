package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.SwapPositionActorCommand;
import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;

import java.util.concurrent.CompletableFuture;

public class SwapRoomPositionUseCase implements UseCase<SwapPositionInput, SwapPositionOutput> {
    private final ActorManager<Game> gameActorManager;

    public SwapRoomPositionUseCase(ActorManager<Game> gameActorManager) {
        this.gameActorManager = gameActorManager;
    }

    public SwapPositionOutput execute(SwapPositionInput input) {
        Actor actor = gameActorManager.get(input.gameId());
        validateActor(actor);

        CompletableFuture<Game> future = actor.enqueueCommand(new SwapPositionActorCommand(input.user(), input.position()));
        Game game = future.join();

        return buildReturn(game);
    }

    private void validateActor(Actor actor) {
        if (actor == null) {
            throw new GameNotFoundException();
        }
    }

    private SwapPositionOutput buildReturn(Game game) {
        return new SwapPositionOutput(
                game
        );
    }
}
