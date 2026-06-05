package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.game.domain.event.TrapTriggeredEvent;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;

public class TrapEffect implements CellEffect {
    private final String ownerId;

    public TrapEffect(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public InteractResult onInteract(GameAction action, String player, Cell cell) {
        boolean isOwner = player.equals(ownerId);

        if (isOwner && !(action instanceof RevealCellAction)) {
            throw new InvalidPlayerActionException();
        }

        cell.clearEffect();

        return new InteractResult(isOwner, new Event(
                StateEvent.TRAP_TRIGGERED,
                new TrapTriggeredEvent(
                        cell.getPosition(),
                        player
                )
        ));
    }
}
