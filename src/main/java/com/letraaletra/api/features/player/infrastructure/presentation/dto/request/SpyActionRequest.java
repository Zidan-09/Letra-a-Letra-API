package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SpyActionRequest(
        @NotBlank
        String actionId,

        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionRequest {
}
