package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.PositionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RevealActionRequest(
        @Valid
        @NotNull
        PositionRequest position
) implements PlayerActionRequest {
}
