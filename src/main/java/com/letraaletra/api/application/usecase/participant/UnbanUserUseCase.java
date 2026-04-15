package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.UnbanParticipantActorCommand;
import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorRepository;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.security.TokenService;

import java.util.concurrent.CompletableFuture;

public class UnbanUserUseCase {
    private final TokenService tokenService;
    private final ActorRepository gameActorRepository;

    public UnbanUserUseCase(TokenService tokenService, ActorRepository gameActorRepository) {
        this.tokenService = tokenService;
        this.gameActorRepository = gameActorRepository;
    }

    public UnbanParticipantOutput execute(UnbanParticipantCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Actor actor = gameActorRepository.getOrCreate(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new UnbanParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        return buildReturn(game);
    }

    private UnbanParticipantOutput buildReturn(Game game) {
        return new UnbanParticipantOutput(game);
    }
}
