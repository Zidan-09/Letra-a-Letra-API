package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record RevokeSessionInput(
        AuthenticatedUser principal
) {
}
