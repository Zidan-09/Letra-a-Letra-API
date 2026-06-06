package com.letraaletra.api.features.user.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public record ChangeCosmeticResponse(
        List<InventoryItem> inventoryItems
) {
}
