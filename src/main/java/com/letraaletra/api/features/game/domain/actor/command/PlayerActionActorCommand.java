package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.actor.output.PlayerActionResult;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.event.TurnPassedEvent;
import com.letraaletra.api.features.game.domain.exception.GameNotRunningException;
import com.letraaletra.api.features.game.domain.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.game.domain.state.GameState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerActionActorCommand implements ActorCommand<PlayerActionResult> {
    private final UUID user;
    private final GameAction action;
    private final GameTimeoutManager gameTimeoutManager;
    private final TurnTimeoutManager turnTimeoutManager;

    public PlayerActionActorCommand(UUID user, GameAction action, GameTimeoutManager gameTimeoutManager, TurnTimeoutManager turnTimeoutManager) {
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

        List<Event> events = action.execute(state, user);

        if (events == null) {
            events = new ArrayList<>();
        }

        GameOverResult gameOverResult = state.gameOverChecker();

        if (gameOverResult.finished()) {
            if (game.getGameType().equals(GameType.CUSTOM)) {
                game.setGameStatus(GameStatus.WAITING);
                gameTimeoutManager.start(game);

            } else {
                game.setGameStatus(GameStatus.CLOSED);
            }

            return new PlayerActionResult(events, gameOverResult, game);
        }

        Player current;
        do {
            state.nextTurn(Instant.now());

            current = state.getPlayerOrThrow(state.currentPlayerTurn());

            if (current.canNotPlay()) {
                events.add(new Event(
                        StateEvent.TURN_PASSED,
                        new TurnPassedEvent(current.getUserId().toString())
                ));
            }

        } while (current.canNotPlay());

        updateTurnEnds(state, events.size());

        turnTimeoutManager.start(game);

        return new PlayerActionResult(events, gameOverResult, game);
    }

    private void validatePlayer(UUID userId, Game game) {
        Participant participant = game.getParticipantByUserId(userId);

        if (participant == null) {
            throw new PlayerNotInGameException();
        }

        if (participant.getRole().equals(ParticipantRole.SPECTATOR)) {
            throw new SpectatorCanNotPlayException();
        }
    }

    private void updateTurnEnds(GameState state, int qtyEvents) {
        Instant finalTime = Instant.now().plusSeconds(45 + (qtyEvents * 2L));

        state.setTurnEndsAt(finalTime);
    }
}
