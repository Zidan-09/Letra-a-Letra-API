package com.letraaletra.api.features.power.domain.actions;

import com.letraaletra.api.features.game.domain.event.CellUnblockedEvent;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.game.domain.board.cell.exception.CellAlreadyRevealedException;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.features.player.domain.exception.NotYourTurnException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnblockCellAction implements GameAction {
    private final String powerId;
    private final Position position;

    public UnblockCellAction(String powerId, Position position) {
        this.powerId = powerId;
        this.position = position;
    }

    @Override
    public List<Event> execute(GameState state, UUID userId) {
        validatePlayerTurn(state, userId);

        Cell cell = state.getBoard().getCell(position);
        validateCell(cell);

        Player player = state.getPlayerOrThrow(userId);
        validatePower(player);

        player.resetPassedTurn();

        player.removeFromInventoryOrThrow(powerId);

        cell.clearEffect();

        return new ArrayList<>(List.of(new Event(
                StateEvent.CELL_UNBLOCKED,
                new CellUnblockedEvent(
                        position,
                        userId.toString()
                )
        )));
    }

    private void validatePlayerTurn(GameState state, UUID userId) {
        if (!state.currentPlayerTurn().equals(userId)) {
            throw new NotYourTurnException();
        }
    }

    private void validatePower(Player player) {
        PowerType power = player.getInventory().get(powerId);

        if (power != PowerType.UNBLOCK) {
            throw new InvalidPlayerActionException();
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
}
