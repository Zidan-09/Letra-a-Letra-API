package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.domain.event.StateEvent;
import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.game.domain.event.TrapTriggeredEvent;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;

import java.util.UUID;

public class TrapEffect implements CellEffect {
    private final UUID ownerId;

    public TrapEffect(UUID ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public InteractResult onInteract(GameAction action, UUID player, Cell cell) {
        boolean isOwner = player.equals(ownerId);

        if (isOwner && !(action instanceof RevealCellAction)) {
            throw new InvalidPlayerActionException();
        }

        cell.clearEffect();

        return new InteractResult(isOwner, new Event(
                StateEvent.TRAP_TRIGGERED,
                new TrapTriggeredEvent(
                        cell.getPosition(),
                        player.toString()
                )
        ));
    }
}
