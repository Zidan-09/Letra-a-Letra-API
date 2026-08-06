package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.features.user.domain.BanType;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record BanUserInput(
        AuthenticatedUser principal,
        UUID userId,
        BanType type,
        int expiresIn,
        String reason
) {
}
