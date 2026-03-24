package com.letraaletra.api.dto.request.websocket.playeractions;

import com.letraaletra.api.domain.position.Position;

public record TrapAction(
        Position position
) implements PlayerAction {
}
