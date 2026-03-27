package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

import com.letraaletra.api.domain.position.Position;

public record TrapActionDTO(
        Position position
) implements PlayerActionDTO {
}
