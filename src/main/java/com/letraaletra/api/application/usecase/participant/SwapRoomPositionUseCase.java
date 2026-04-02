package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.SwapPositionCommand;
import com.letraaletra.api.application.output.participant.SwapPositionOutput;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.GameIsRunningException;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.exception.UserNotInGameException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwapRoomPositionUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    public SwapPositionOutput execute(SwapPositionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);

        Participant participant = game.getParticipantByUserId(command.user());

        validateParticipant(participant);

        game.changePosition(command.user(), command.position());

        gameRepository.save(game);

        return buildReturn(game, command.token());
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

    private SwapPositionOutput buildReturn(Game game, String token) {
        return new SwapPositionOutput(
                token,
                game
        );
    }
}
