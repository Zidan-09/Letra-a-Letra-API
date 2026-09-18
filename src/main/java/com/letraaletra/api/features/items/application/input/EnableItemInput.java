package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record EnableItemInput(
        AuthenticatedUser principal,
        UUID itemId
) {
}
