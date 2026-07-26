package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record DisableCosmeticInput(
        AuthenticatedUser principal,
        UUID id
) {
}
