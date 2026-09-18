package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.EquippableCategory;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.util.UUID;

public record InventoryItemResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        int quantity,
        boolean equipped,
        String assetPath
) {
}
