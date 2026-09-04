package com.letraaletra.api.features.game.domain.actor.command;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.result.DiscardPowerResult;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.event.TurnPassedEvent;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.features.player.domain.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscardPowerActorCommand implements ActorCommand<DiscardPowerResult> {
    private final UUID userId;
    private final String powerId;
    private final TurnTimeoutManager turnTimeoutManager;

    public DiscardPowerActorCommand(UUID userId, String powerId, TurnTimeoutManager turnTimeoutManager) {
        this.userId = userId;
        this.powerId = powerId;
        this.turnTimeoutManager = turnTimeoutManager;
    }

    public DiscardPowerActorCommand(UUID userId, String powerId) {
        this(userId, powerId, null);
    }

    @Override
    public DiscardPowerResult execute(Game game) {
        GameState state = game.getGameState();
        Player player = state.getPlayerOrThrow(userId);

        player.removeFromInventoryOrThrow(powerId);

        List<Event> events = new ArrayList<>();

        if (player.isFrozen() && !player.hasFreezeDefense() && state.currentPlayerTurn().equals(userId)) {
            events.add(new Event(
                    StateEvent.TURN_PASSED,
                    new TurnPassedEvent(userId.toString())
            ));

            state.nextTurn(Instant.now());

            Player current;
            do {
                current = state.getPlayerOrThrow(state.currentPlayerTurn());
                if (current.canNotPlay()) {
                    events.add(new Event(
                            StateEvent.TURN_PASSED,
                            new TurnPassedEvent(current.getUserId().toString())
                    ));
                    state.nextTurn(Instant.now());
                } else {
                    break;
                }
            } while (current.canNotPlay());

            Instant finalTime = Instant.now().plusSeconds(45 + (events.size() * 2L));
            state.setTurnEndsAt(finalTime);

            if (turnTimeoutManager != null) {
                turnTimeoutManager.start(game);
            }
        }

        return new DiscardPowerResult(game, events);
    }
}
