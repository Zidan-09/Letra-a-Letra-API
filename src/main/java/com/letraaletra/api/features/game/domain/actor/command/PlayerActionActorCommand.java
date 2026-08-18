package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.actor.result.PlayerActionResult;
import com.letraaletra.api.features.game.domain.service.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.event.TurnPassedEvent;
import com.letraaletra.api.features.game.domain.exception.GameNotRunningException;
import com.letraaletra.api.features.game.domain.exception.SpectatorCanNotPlayException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.board.power.actions.GameAction;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerActionActorCommand implements ActorCommand<PlayerActionResult> {
    private final UUID userId;
    private final GameAction action;
    private final TurnTimeoutManager turnTimeoutManager;
    private final UserRepository userRepository;

    public PlayerActionActorCommand(
            UUID userId,
            GameAction action,
            TurnTimeoutManager turnTimeoutManager,
            UserRepository userRepository
    ) {
        this.userId = userId;
        this.action = action;
        this.turnTimeoutManager = turnTimeoutManager;
        this.userRepository = userRepository;
    }

    @Override
    public PlayerActionResult execute(Game game) {
        if (!(game.getGameStatus().equals(GameStatus.RUNNING)) || game.getGameState() == null) {
            throw new GameNotRunningException();
        }

        validatePlayer(userId, game);

        GameState state = game.getGameState();

        List<Event> events = action.execute(state, userId);

        if (events == null) {
            events = new ArrayList<>();
        }

        Optional<GameOver> gameOver = state.gameOverBecauseScore();

        if (gameOver.isPresent()) {
            if (game.getGameType().equals(GameType.CUSTOM)) {
                game.setGameStatus(GameStatus.WAITING);

            } else {
                List<UUID> userIds = game.getParticipants().getIds();

                List<User> userList = userRepository.findUsersById(userIds);

                for (User user : userList) {
                    user.leaveGame();
                }

                userRepository.saveAll(userList);

                game.setGameStatus(GameStatus.CLOSED);
            }

            return new PlayerActionResult(events, gameOver, game);
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

        return new PlayerActionResult(events, gameOver, game);
    }

    private void validatePlayer(UUID userId, Game game) {
        Participant participant = game.getParticipants().getParticipantByUserId(userId);

        if (participant == null) {
            throw new PlayerNotInGameException();
        }

        if (participant.isSpectator()) {
            throw new SpectatorCanNotPlayException();
        }
    }

    private void updateTurnEnds(GameState state, int qtyEvents) {
        Instant finalTime = Instant.now().plusSeconds(45 + (qtyEvents * 2L));

        state.setTurnEndsAt(finalTime);
    }
}
