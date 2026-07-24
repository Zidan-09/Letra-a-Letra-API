package com.letraaletra.api.features.participant.application.service;

import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.exception.InvalidModerateActionException;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;

import java.util.UUID;

public class ModerationContextService {
    private final ActorManager<Game> actorManager;

    public ModerationContextService(
            ActorManager<Game> actorManager
    ) {
        this.actorManager = actorManager;
    }

    public ModerationContext resolve(UUID gameId, UUID targetId, UUID hostId) {
        Game game = actorManager.get(gameId).getGame();

        validateUser(hostId, game);
        validateAction(targetId, hostId);

        Participant participant = game.getParticipants().getParticipantByUserId(targetId);

        validateParticipant(participant);

        return new ModerationContext(
                game,
                participant
        );
    }

    private void validateAction(UUID targetId, UUID hostId) {
        if (targetId.equals(hostId)) {
            throw new InvalidModerateActionException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private void validateUser(UUID userId, Game game) {
        if (!game.getHostId().equals(userId)) {
            throw new OnlyHostCanModerateException();
        }
    }
}
