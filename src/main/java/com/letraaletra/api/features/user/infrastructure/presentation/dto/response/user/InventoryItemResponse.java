package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemKind;

import java.util.UUID;

public record InventoryItemResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        ItemContext context,
        int quantity,
        boolean equipped,
        String assetPath
) {
}
