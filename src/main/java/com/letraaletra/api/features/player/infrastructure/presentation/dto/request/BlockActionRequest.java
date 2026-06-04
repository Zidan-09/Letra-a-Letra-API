package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.presentation.dto.request.game.PositionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@JsonTypeName("BLOCK")

public record BlockActionRequest(
        @NotBlank
        String actionId,

        @Valid
        @NotNull
        PositionDTO position
) implements PlayerActionRequest {
}
