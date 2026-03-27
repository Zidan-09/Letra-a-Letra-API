package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

public record BlindActionDTO(
        String targetId
) implements PlayerActionDTO {
}
