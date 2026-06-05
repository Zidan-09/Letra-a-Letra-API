package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record CellTrappedEvent(
        Position cell,
        String trappedBy
) implements EventData {
}
