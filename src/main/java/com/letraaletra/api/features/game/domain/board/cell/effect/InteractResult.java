package com.letraaletra.api.features.game.domain.board.cell.effect;

import com.letraaletra.api.features.game.domain.event.Event;

public record InteractResult(
        boolean canContinue,
        Event event
) {
}
