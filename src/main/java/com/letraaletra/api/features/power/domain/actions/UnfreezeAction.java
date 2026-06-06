package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.PlayerUnfreezeEvent;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.effect.FreezeEffect;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.features.player.domain.exception.NotYourTurnException;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;

public class UnfreezeAction implements GameAction {
    private final String powerId;

    public UnfreezeAction(String powerId) {
        this.powerId = powerId;
    }

    @Override
    public List<Event> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);

        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        player.removeFromInventoryOrThrow(powerId);

        player.removeEffect(FreezeEffect.class);

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_UNFREEZE,
                new PlayerUnfreezeEvent(
                        userId
                )
        )));
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.UNFREEZE) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }
}
