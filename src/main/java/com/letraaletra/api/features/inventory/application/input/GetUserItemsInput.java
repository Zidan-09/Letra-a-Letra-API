package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.util.UUID;

public record GetUserItemsInput(
        UUID userId,
        ItemKind kind,
        ItemCategory category,
        ItemContext context,
        Boolean equipped
) {
}
