package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ToggleItemAvailabilityRequest(
        @NotNull Boolean available
) {
}
