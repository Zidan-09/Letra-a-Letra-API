package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public record GetUserInventoryOutput(
        List<InventoryItem> inventory
) {
}
