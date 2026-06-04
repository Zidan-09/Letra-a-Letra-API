package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

@JsonTypeName("DETECT_TRAPS")
public record DetectTrapsActionRequest(
        @NotBlank
        String actionId
) implements PlayerActionRequest {
}
