package com.letraaletra.api.presentation.dto.request.game;

import com.letraaletra.api.domain.game.GameMode;
import jakarta.validation.constraints.NotBlank;

public record GameSettingsDTO(
        @NotBlank
        String themeId,

        @NotBlank
        GameMode gameMode
) {
}
