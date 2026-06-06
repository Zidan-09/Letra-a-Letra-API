package com.letraaletra.api.features.game.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record RoomSettingsDTO(
        @NotNull
        boolean allowSpectators,

        @NotNull
        boolean privateGame
) {
}
