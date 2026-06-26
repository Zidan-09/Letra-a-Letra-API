package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.PlayerAreImmuneEvent;
import com.letraaletra.api.features.game.domain.event.PlayerFrozenEvent;
import com.letraaletra.api.features.player.domain.effect.ImmunityEffect;
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
import java.util.UUID;

public class FreezePlayerAction implements GameAction {
    private final String powerId;
    private final UUID target;

    public FreezePlayerAction(String powerId, UUID target) {
        this.powerId = powerId;
        this.target = target;
    }

    @Override
    public List<Event> execute(GameState state, UUID userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        Player opponent = state.getPlayerOrThrow(target);
        validatePlayer(opponent);

        player.removeFromInventoryOrThrow(powerId);

        if (isImmune(opponent)) {
            return new ArrayList<>(List.of(new Event(
                    StateEvent.PLAYER_ARE_IMMUNE,
                    new PlayerAreImmuneEvent(target)
            )));
        }

        opponent.applyEffect(new FreezeEffect());

        return new ArrayList<>(List.of(new Event(
                StateEvent.PLAYER_FROZEN,
                new PlayerFrozenEvent(target.toString())
        )));
    }

    private void validatePlayerTurn(GameState state, UUID userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.FREEZE) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }

    private boolean isImmune(Player player) {
        return player.getEffects().stream()
                .anyMatch(effect -> effect instanceof ImmunityEffect);
    }
}
