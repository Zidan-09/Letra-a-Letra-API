package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.UnbanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class UnbanUserUseCase implements UseCase<UnbanParticipantInput, UnbanParticipantOutput> {
    private final ActorManager<Game> gameActorManager;

    public UnbanUserUseCase(ActorManager<Game> gameActorManager) {
        this.gameActorManager = gameActorManager;
    }

    public UnbanParticipantOutput execute(UnbanParticipantInput input) {
        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Game> future = actor.enqueueCommand(new UnbanParticipantActorCommand(input.target(), input.user()));
        Game game = future.join();

        return buildReturn(game);
    }

    private UnbanParticipantOutput buildReturn(Game game) {
        return new UnbanParticipantOutput(game);
    }
}
