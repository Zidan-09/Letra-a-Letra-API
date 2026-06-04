package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.letraaletra.api.presentation.dto.request.game.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RevealActionRequest(
        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionRequest {
}
