package com.letraaletra.api.features.user.application.output;

import java.util.UUID;

public record CreateUserOutput(
        UUID id,
        String nickname,
        String email
) {
}
