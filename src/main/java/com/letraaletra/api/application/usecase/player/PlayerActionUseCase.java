package com.letraaletra.api.application.usecase.player;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;
import com.letraaletra.api.domain.game.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import com.letraaletra.api.domain.game.exception.GameNotStartedException;
import com.letraaletra.api.domain.repository.GameRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class PlayerActionUseCase {
    private final GameRepository gameRepository;
    private final TokenService tokenService;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public PlayerActionUseCase(GameRepository gameRepository, TokenService tokenService, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    public PlayerActionOutput execute(PlayerActionCommand command) {
        String gameId = tokenService.getTokenContent(command.token());

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validatePlayer(command.user(), game);

        GameState state = game.getGameState();

        Optional<List<StateEvent>> event = command.action().execute(state, command.user());

        GameOverResult gameOverResult = state.gameOverChecker();
        handleGameOver(game, gameOverResult);

        Instant now = Instant.now().plusSeconds(45);

        state.nextTurn(now);

        gameRepository.save(game);

        turnTimeoutManager.start(game);

        return buildOutput(game, gameOverResult, event.orElse(null));
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

    private void handleGameOver(Game game, GameOverResult result) {
        if (!result.finished()) return;

        game.setGameStatus(GameStatus.WAITING);

        gameTimeoutManager.start(game);
    }

    private PlayerActionOutput buildOutput(Game game, GameOverResult gameOverResult, List<StateEvent> event) {
        return new PlayerActionOutput(
                game,
                event,
                gameOverResult.finished() ? Optional.of(gameOverResult) : Optional.empty()
        );
    }
}
