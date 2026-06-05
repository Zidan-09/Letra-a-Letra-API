package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record CellUnblockedEvent(
        Position cell,
        String unblockedBy
) implements EventData {
}
