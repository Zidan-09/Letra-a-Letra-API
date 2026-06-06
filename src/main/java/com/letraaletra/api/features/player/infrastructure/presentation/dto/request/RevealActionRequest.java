package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RevealActionRequest(
        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionRequest {
}
