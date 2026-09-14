package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.inventory.domain.ItemContext;

import java.util.UUID;

public record ConsumeItemInput(
        UUID userId,
        UUID itemId,
        Integer quantity,
        ItemContext context
) {
}
