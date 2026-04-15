package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.SwapPositionActorCommand;
import com.letraaletra.api.application.command.participant.SwapPositionCommand;
import com.letraaletra.api.application.output.participant.SwapPositionOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;

import java.util.concurrent.CompletableFuture;

public class SwapRoomPositionUseCase {
    private final ActorRepository gameActorRepository;
    private final TokenService tokenService;

    public SwapRoomPositionUseCase(ActorRepository gameActorRepository, TokenService tokenService) {
        this.gameActorRepository = gameActorRepository;
        this.tokenService = tokenService;
    }

    public SwapPositionOutput execute(SwapPositionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorRepository.getOrCreate(gameId);
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
