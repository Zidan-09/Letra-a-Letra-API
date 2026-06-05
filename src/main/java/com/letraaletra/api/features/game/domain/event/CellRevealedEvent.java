package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record CellRevealedEvent(
        Position cell,
        String revealedBy
) implements EventData {
}
