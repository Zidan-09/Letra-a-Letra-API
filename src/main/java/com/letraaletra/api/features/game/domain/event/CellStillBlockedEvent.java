package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record CellStillBlockedEvent(
        Position cell,
        String blockedBy,
        int remainingAttempts
) implements EventData {
}
