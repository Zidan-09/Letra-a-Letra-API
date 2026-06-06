package com.letraaletra.api.features.game.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.game.domain.state.GameMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameSettingsDTO(
        @NotBlank
        String themeId,

        @NotNull
        GameMode gameMode
) {
}
