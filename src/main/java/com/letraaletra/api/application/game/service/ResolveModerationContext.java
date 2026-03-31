package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameIsRunningException;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.OnlyHostCanKickException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResolveModerationContext {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    public ModerationContext resolve(String tokenGameId, String participantId, String userId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validateUser(userId, game);

        Participant participant = game.getParticipantByUserId(participantId);

        validateParticipant(participant);

        return new ModerationContext(
                game,
                participant
        );
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
            throw new OnlyHostCanKickException();
        }
    }
}
