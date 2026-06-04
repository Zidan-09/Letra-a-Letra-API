package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("BLIND")
public record BlindActionRequest(
        @NotBlank
        String actionId,

        @NotBlank
        String targetId
) implements PlayerActionRequest {
}
