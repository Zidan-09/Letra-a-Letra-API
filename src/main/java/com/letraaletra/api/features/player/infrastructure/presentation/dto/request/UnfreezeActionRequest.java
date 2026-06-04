package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UnfreezeActionRequest(
        @NotBlank
        String actionId
) implements PlayerActionRequest {
}
