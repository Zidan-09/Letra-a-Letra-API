package com.letraaletra.api.dto.request.websocket;

import com.letraaletra.api.domain.game.GameMode;
import jakarta.validation.constraints.NotBlank;

public record GameSettingsDTO(
        @NotBlank
        String themeId,

        @NotBlank
        GameMode gameMode,

        @NotBlank
        boolean allowSpectators,

        @NotBlank
        boolean privateGame
) {
}
