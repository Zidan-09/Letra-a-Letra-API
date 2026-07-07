package com.letraaletra.api.features.cosmetic.application.input;

import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public record DisableCosmeticInput(
        UUID auth,
        UUID id
) {
}
