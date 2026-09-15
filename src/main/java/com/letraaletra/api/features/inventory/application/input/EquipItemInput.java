package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.items.domain.ItemContext;

import java.util.UUID;

public record EquipItemInput(
        UUID userId,
        UUID itemId,
        ItemContext context
) {
}
