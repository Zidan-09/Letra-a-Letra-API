package com.letraaletra.api.application.command.actor;

import com.letraaletra.api.application.output.actor.PlayerActionResult;
import com.letraaletra.api.application.port.GameTimeoutManager;
import com.letraaletra.api.application.port.TurnTimeoutManager;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.exception.GameNotRunningException;
import com.letraaletra.api.domain.game.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.participant.ParticipantRole;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;
import com.letraaletra.api.domain.game.service.GameOverResult;

import java.time.Instant;
import java.util.List;

public class PlayerActionActorCommand implements ActorCommand<PlayerActionResult> {
    private final String user;
    private final GameAction action;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public PlayerActionActorCommand(String user, GameAction action, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
        this.user = user;
        this.action = action;
        this.gameTimeoutManager = gameTimeoutManager;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    @Override
    public PlayerActionResult execute(Game game) {
        if (!(game.getGameStatus().equals(GameStatus.RUNNING))) {
            throw new GameNotRunningException();
        }

        validatePlayer(user, game);

        GameState state = game.getGameState();

        List<StateEvent> event = action.execute(state, user);

        GameOverResult gameOverResult = state.gameOverChecker();

        if (gameOverResult.finished()) {
            game.setGameStatus(GameStatus.WAITING);

            gameTimeoutManager.start(game);

            return new PlayerActionResult(event, gameOverResult, game);
        }

        Instant now = Instant.now().plusSeconds(45);

        state.nextTurn(now);

        Player current;

        do {
            current = state.getPlayerOrThrow(state.currentPlayerTurn());
            if (current.canNotPlay()) {
                event.add(StateEvent.TURN_PASSED);
                state.nextTurn(now);
            }
        } while (current.canNotPlay());

        turnTimeoutManager.start(game);

        return new PlayerActionResult(event, gameOverResult, game);
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
}
