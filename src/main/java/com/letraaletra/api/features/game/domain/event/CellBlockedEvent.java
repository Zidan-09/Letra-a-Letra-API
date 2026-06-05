package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record CellBlockedEvent(
        Position cell,
        String blockedBy
) implements EventData {
}
