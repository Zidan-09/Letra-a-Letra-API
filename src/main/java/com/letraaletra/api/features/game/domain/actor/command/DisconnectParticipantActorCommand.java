package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.application.port.DisconnectScheduler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;

import java.util.Optional;

public class DisconnectParticipantActorCommand implements ActorCommand<Optional<Game>> {
    private final String userId;
    private final DisconnectScheduler disconnectScheduler;

    public DisconnectParticipantActorCommand(String userId, DisconnectScheduler disconnectScheduler) {
        this.userId = userId;
        this.disconnectScheduler = disconnectScheduler;
    }

    @Override
    public Optional<Game> execute(Game game) {
        Participant participant = game.getParticipantByUserId(userId);

        if (participant != null) {
            disconnectScheduler.start(userId, game.getId());

            participant.disconnect();

            return Optional.of(game);
        }

        return Optional.empty();
    }
}
