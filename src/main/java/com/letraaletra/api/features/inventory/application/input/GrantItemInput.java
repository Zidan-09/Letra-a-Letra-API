package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record GrantItemInput(
        AuthenticatedUser principal,
        UUID userId,
        UUID itemId,
        Integer quantity
) {
}
