package com.letraaletra.api.features.participant.application.service;

import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.exception.InvalidModerateActionException;
import com.letraaletra.api.features.participant.domain.exception.OnlyHostCanModerateException;
import com.letraaletra.api.shared.domain.security.TokenService;

import java.util.UUID;

public class ModerationContextService {
    private final ActorManager<Game> actorManager;
    private final TokenService tokenService;

    public ModerationContextService(
            ActorManager<Game> actorManager,
            TokenService tokenService
    ) {
        this.actorManager = actorManager;
        this.tokenService = tokenService;
    }

    public ModerationContext resolve(String tokenGameId, UUID targetId, UUID hostId) {
        UUID gameId = tokenService.getTokenContent(tokenGameId);

        Game game = actorManager.get(gameId.toString()).getGame();

        validateUser(hostId, game);
        validateAction(targetId, hostId);

        Participant participant = game.getParticipantByUserId(targetId);

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
