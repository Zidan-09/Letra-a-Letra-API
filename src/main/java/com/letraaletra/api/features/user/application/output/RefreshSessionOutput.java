package com.letraaletra.api.features.user.application.output;

import java.util.UUID;

public record RefreshSessionOutput(
        UUID id,
        String token,
        String refreshToken
) {
}
