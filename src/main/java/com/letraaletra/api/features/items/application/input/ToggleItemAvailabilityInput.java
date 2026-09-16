package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record ToggleItemAvailabilityInput(
        AuthenticatedUser principal,
        UUID itemId,
        Boolean available
) {
}
