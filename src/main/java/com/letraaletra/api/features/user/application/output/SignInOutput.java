package com.letraaletra.api.features.user.application.output;

import java.util.UUID;

public record SignInOutput(
        UUID id,
        String token
) {
}
