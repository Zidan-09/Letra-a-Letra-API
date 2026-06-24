package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

import java.util.UUID;

public record CellTrappedEvent(
        Position cell,
        UUID trappedBy
) implements EventData {
}
