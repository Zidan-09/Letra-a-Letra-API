package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.board.cell.Cell;
import com.letraaletra.api.features.power.domain.actions.GameAction;

public interface CellEffect {
    String getOwnerId();
    InteractResult onInteract(GameAction action, String player, Cell cell);
}
