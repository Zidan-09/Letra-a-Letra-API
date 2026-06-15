package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.UnbanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;

import java.util.concurrent.CompletableFuture;

public class UnbanUserUseCase implements UseCase<UnbanParticipantInput, UnbanParticipantOutput> {
    private final TokenService tokenService;
    private final ActorManager<Game> gameActorManager;

    public UnbanUserUseCase(TokenService tokenService, ActorManager<Game> gameActorManager) {
        this.tokenService = tokenService;
        this.gameActorManager = gameActorManager;
    }

    public UnbanParticipantOutput execute(UnbanParticipantInput input) {
        String gameId = tokenService.getTokenContent(input.token());

        Actor actor = gameActorManager.get(gameId);

        CompletableFuture<Game> future = actor.enqueueCommand(new UnbanParticipantActorCommand(input.target(), input.user()));
        Game game = future.join();

        return buildReturn(game);
    }

    private UnbanParticipantOutput buildReturn(Game game) {
        return new UnbanParticipantOutput(game);
    }
}
