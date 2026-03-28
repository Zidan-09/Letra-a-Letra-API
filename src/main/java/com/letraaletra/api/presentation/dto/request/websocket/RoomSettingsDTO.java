package com.letraaletra.api.presentation.dto.request.websocket;

import jakarta.validation.constraints.NotNull;

public record RoomSettingsDTO(
        @NotNull
        boolean allowSpectators,

        @NotNull
        boolean privateGame
) {
}
