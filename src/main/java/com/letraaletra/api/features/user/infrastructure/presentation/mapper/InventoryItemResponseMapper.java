package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;

public class InventoryItemResponseMapper {
    public static InventoryItemResponse toResponse(InventoryItem inventoryItem) {
        return new InventoryItemResponse(
                inventoryItem.cosmeticId(),
                inventoryItem.name(),
                inventoryItem.type(),
                inventoryItem.equipped()
        );
    }
}
