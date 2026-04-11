package com.letraaletra.api.domain.game.player.actions;

import com.letraaletra.api.domain.game.GameState;
import com.letraaletra.api.domain.game.StateEvent;
import com.letraaletra.api.domain.game.board.cell.Cell;
import com.letraaletra.api.domain.game.board.cell.PowerType;
import com.letraaletra.api.domain.game.board.cell.effect.CellEffect;
import com.letraaletra.api.domain.game.board.cell.effect.TrapEffect;
import com.letraaletra.api.domain.game.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.Player;
import com.letraaletra.api.domain.game.player.exception.InvalidPlayerActionException;
import com.letraaletra.api.domain.game.player.exception.NotYourTurnException;
import com.letraaletra.api.domain.game.player.exception.PlayerNotInGameException;

import java.util.List;

public class TrapCellAction implements GameAction {
    private final Position position;
    private final String powerId;

    public TrapCellAction(Position position, String powerId) {
        this.position = position;
        this.powerId = powerId;
    }

    @Override
    public List<StateEvent> execute(GameState state, String userId) {
        validatePlayerTurn(state, userId);

        Player player = state.getPlayerOrThrow(userId);
        validatePlayer(player);
        validatePower(player);

        Cell cell = state.getBoard().getCell(position);

        validateCell(cell);

        boolean canContinue = activateEffect(cell, userId);

        if (!canContinue) return null;

        cell.setEffect(
                new TrapEffect(userId)
        );

        state.getPlayerOrThrow(userId).removeFromInventory(powerId);

        return List.of(StateEvent.CELL_TRAPPED);
    }

    private void validatePlayerTurn(GameState state, String userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validateCell(Cell cell) {
        if (cell == null) {
            throw new InvalidPlayerActionException();
        }

        if (cell.isRevealed()) {
            throw new CellAlreadyRevealedException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.TRAP) {
            throw new InvalidPlayerActionException();
        }
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new PlayerNotInGameException();
        }
    }

    private boolean activateEffect(Cell cell, String player) {
        if (cell.hasEffect()) {
            CellEffect effect = cell.getEffect();

            if (!(effect instanceof TrapEffect))  return true;

            return effect.onInteract(this, player, cell);
        }

        return true;
    }
}
