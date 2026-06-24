package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.event.CellStillBlockedEvent;
import com.letraaletra.api.features.game.domain.event.CellUnblockedEvent;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;

import java.util.UUID;

public class BlockEffect implements CellEffect {
    private final UUID ownerId;
    private int remainingAttempts = 3;

    public BlockEffect(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void registerAttempt() {
        remainingAttempts = Math.max(0, remainingAttempts - 1);
    }

    @Override
    public InteractResult onInteract(GameAction action, UUID player, Cell cell) {
        if (!(action instanceof RevealCellAction)) {
            throw new InvalidPlayerActionException();
        }

        registerAttempt();

        if (remainingAttempts > 0) {
            return new InteractResult(false, new Event(
                    StateEvent.CELL_STILL_BLOCKED,
                    new CellStillBlockedEvent(
                            cell.getPosition(),
                            ownerId.toString(),
                            remainingAttempts
                    )
            ));
        }

        cell.clearEffect();
        return new InteractResult(true, new Event(
                StateEvent.CELL_UNBLOCKED,
                new CellUnblockedEvent(
                        cell.getPosition(),
                        player.toString()
                )
        ));
    }
}
