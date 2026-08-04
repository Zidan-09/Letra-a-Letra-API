package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.participant.domain.Participant;

import java.util.Optional;
import java.util.UUID;

public class DisconnectParticipantActorCommand implements ActorCommand<Optional<Game>> {
    private final UUID userId;

    public DisconnectParticipantActorCommand(UUID userId) {
        this.userId = userId;
    }

    @Override
    public Optional<Game> execute(Game game) {
        try {
            Participant participant = game.getParticipants()
                    .getParticipantByUserId(userId);

            participant.disconnect();

            return Optional.of(game);
        } catch (UserNotInGameException e) {
            return Optional.empty();
        }
    }
}
