package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

import jakarta.validation.constraints.NotBlank;

public record BlindActionDTO(
        @NotBlank
        String targetId
) implements PlayerActionDTO {
}
