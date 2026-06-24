package com.letraaletra.api.features.user.application.input;

import java.util.UUID;

public record ChangeCosmeticInput(
        UUID userId,
        String cosmeticId
) {
}
