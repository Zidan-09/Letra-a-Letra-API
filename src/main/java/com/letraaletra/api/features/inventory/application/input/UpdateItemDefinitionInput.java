package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record UpdateItemDefinitionInput(
        AuthenticatedUser principal,
        UUID itemId,
        String name,
        String assetPath,
        Boolean available
) {
}
