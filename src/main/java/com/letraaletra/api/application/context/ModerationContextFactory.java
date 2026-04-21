package com.letraaletra.api.application.context;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.*;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.exception.InvalidModerateActionException;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanModerateException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;

public class ModerationContextFactory {
    private final GameRepository gameRepository;
    private final TokenService tokenService;

    public ModerationContextFactory(GameRepository gameRepository, TokenService tokenService) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
    }

    public ModerationContext resolve(String tokenGameId, String targetId, String hostId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId).orElse(null);

        validateGame(game);
        validateUser(hostId, game);
        validateAction(targetId, hostId);

        Participant participant = game.getParticipantByUserId(targetId);

        validateParticipant(participant);

        return new ModerationContext(
                game,
                participant
        );
    }

    private void validateAction(String targetId, String hostId) {
        if (targetId.equals(hostId)) {
            throw new InvalidModerateActionException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus().equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private void validateUser(String userId, Game game) {
        if (!game.getHostId().equals(userId)) {
            throw new OnlyHostCanModerateException();
        }
    }
}
