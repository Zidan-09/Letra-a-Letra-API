package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

public record FreezeActionDTO(
        String targetId
) implements PlayerActionDTO {
}
