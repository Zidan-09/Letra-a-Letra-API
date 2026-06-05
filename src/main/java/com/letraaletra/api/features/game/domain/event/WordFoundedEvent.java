package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record WordFoundedEvent(
        Position[] cells,
        String foundedBy
) implements EventData {
}
