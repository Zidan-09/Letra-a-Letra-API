package com.letraaletra.api.features.inventory.application.input;

import java.util.UUID;

public record ConsumeItemInput(
        UUID userId,
        UUID itemId,
        Integer quantity
) {
}
