package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;

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
