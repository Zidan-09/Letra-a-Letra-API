package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.participant.exception.UserNotInGameException;
import com.letraaletra.api.features.game.domain.Game;

import java.util.Optional;
import java.util.UUID;

public class ReconnectParticipantActorCommand implements ActorCommand<Optional<Game>> {
    private final UUID userId;
    private final String sessionId;

    public ReconnectParticipantActorCommand(UUID userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @Override
    public Optional<Game> execute(Game game) {
        try {
            game.getParticipants().reconnect(userId, sessionId);

            return Optional.of(game);
        } catch (UserNotInGameException e) {
            return Optional.empty();
        }
    }
}
