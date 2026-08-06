package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record UnbanUserInput(
        AuthenticatedUser principal,
        UUID userId
) {
}
