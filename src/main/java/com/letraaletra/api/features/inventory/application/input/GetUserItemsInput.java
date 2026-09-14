package com.letraaletra.api.features.inventory.application.input;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemKind;

import java.util.UUID;

public record GetUserItemsInput(
        UUID userId,
        ItemKind kind,
        ItemCategory category,
        ItemContext context,
        Boolean equipped
) {
}
