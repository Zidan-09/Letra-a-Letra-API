package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameIsRunningException;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.OnlyHostCanModerateException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnbanUserUseCase {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    public void execute(String tokenGameId, String participantId, String userId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validateHost(game, userId);

        game.removeFromBlackList(participantId);

        gameRepository.save(game);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus().equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }
    }

    private void validateHost(Game game, String hostId) {
        if (!game.getHostId().equals(hostId)) {
            throw new OnlyHostCanModerateException();
        }
    }
}
