package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.GameIsRunningException;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.participant.exception.OnlyHostCanModerateException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;

public class UnbanUserUseCase {
    private final GameRepository gameRepository;
    private final TokenService tokenService;

    public UnbanUserUseCase(GameRepository gameRepository, TokenService tokenService) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
    }

    public UnbanParticipantOutput execute(UnbanParticipantCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validateHost(game, command.user());

        game.removeFromBlackList(command.target());

        gameRepository.save(game);

        return buildReturn(game);
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

    private UnbanParticipantOutput buildReturn(Game game) {
        return new UnbanParticipantOutput(game);
    }
}
