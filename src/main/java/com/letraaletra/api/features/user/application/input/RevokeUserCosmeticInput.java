package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record RevokeUserCosmeticInput(
        AuthenticatedUser principal,
        UUID userId,
        UUID cosmeticId
) {
}
