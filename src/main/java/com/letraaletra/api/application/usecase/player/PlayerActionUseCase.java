package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;
import com.letraaletra.api.domain.game.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.exception.GameNotStartedException;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerActionUseCase {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    public PlayerActionOutput execute(PlayerActionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validatePlayer(command.user(), game);

        GameState state = game.getGameState();

        command.action().execute(state, command.user());

        gameRepository.save(game);

        return buildReturn(game, gameId);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotStartedException();
        }
    }

    private void validatePlayer(String userId, Game game) {
        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null) {
            throw new PlayerNotInGameException();
        }

        if (participant.getRole().equals(ParticipantRole.SPECTATOR)) {
            throw new SpectatorCanNotPlayException();
        }
    }

    private PlayerActionOutput buildReturn(Game game, String id) {
        return new PlayerActionOutput(
                game
        );
    }
}
