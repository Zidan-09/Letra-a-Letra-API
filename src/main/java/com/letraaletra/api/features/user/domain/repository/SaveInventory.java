package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

public interface SaveInventory {
    InventoryItem save(InventoryItem inventory, String userId);
}
