package com.letraaletra.api.features.game.domain.event;

import com.letraaletra.api.features.game.domain.board.position.Position;

public record TrapTriggeredEvent(
        Position cell,
        String triggeredBy
) implements EventData {
}
