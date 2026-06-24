package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.actions.GameAction;

import java.util.UUID;

public interface CellEffect {
    UUID getOwnerId();
    InteractResult onInteract(GameAction action, UUID player, Cell cell);
}
