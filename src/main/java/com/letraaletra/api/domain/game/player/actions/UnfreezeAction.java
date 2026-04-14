package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.effect.FreezeEffect;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;

import java.util.ArrayList;
import java.util.List;

public class UnfreezeAction implements GameAction {
    private final String powerId;

    public UnfreezeAction(String powerId) {
        this.powerId = powerId;
    }

    @Override
    public List<StateEvent> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);

        validatePlayer(player);
        validatePower(player);

        player.resetPassedTurn();

        player.removeFromInventoryOrThrow(powerId);

        player.removeEffect(FreezeEffect.class);

        return new ArrayList<>(List.of(StateEvent.PLAYER_UNFREEZE));
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
