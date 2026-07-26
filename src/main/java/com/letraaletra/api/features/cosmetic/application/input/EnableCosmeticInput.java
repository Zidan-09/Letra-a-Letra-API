package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record EnableCosmeticInput(
        AuthenticatedUser principal,
        UUID id
) {
}
