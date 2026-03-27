package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.NotBlank;

public record CreateGameWsRequest(
        @NotBlank
        String name,

        @NotBlank
        GameSettingsDTO settings
) implements WsRequestDTO {
}
