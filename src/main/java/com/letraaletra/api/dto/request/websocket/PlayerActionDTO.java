package com.letraaletra.api.dto.request.websocket;

import com.letraaletra.api.domain.position.Position;

public record PlayerActionDTO(
        PlayerActions playerAction,
        Position position,
        String target
) {
}
