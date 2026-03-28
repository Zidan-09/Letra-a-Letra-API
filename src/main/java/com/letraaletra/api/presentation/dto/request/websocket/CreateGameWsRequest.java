package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGameWsRequest(
        @NotBlank
        String name,

        @Valid
        @NotNull
        RoomSettingsDTO settings
) implements WsRequestDTO {
}
