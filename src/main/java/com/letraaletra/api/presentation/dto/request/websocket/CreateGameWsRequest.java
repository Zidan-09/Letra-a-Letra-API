package com.letraaletra.api.presentation.dto.request.websocket;

import com.letraaletra.api.presentation.dto.request.websocket.playeractions.RoomSettingsDTO;
import jakarta.validation.constraints.NotBlank;

public record CreateGameWsRequest(
        @NotBlank
        String name,

        @NotBlank
        RoomSettingsDTO settings
) implements WsRequestDTO {
}
