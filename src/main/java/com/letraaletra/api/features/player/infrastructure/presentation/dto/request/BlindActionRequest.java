package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BlindActionRequest(
        @NotBlank
        String actionId,

        @NotBlank
        String targetId
) implements PlayerActionRequest {
}
