package com.letraaletra.api.domain.game.board.cell.effect;

import com.letraaletra.api.domain.game.StateEvent;

public record InteractResult(
        boolean canContinue,
        StateEvent event
) {
}
