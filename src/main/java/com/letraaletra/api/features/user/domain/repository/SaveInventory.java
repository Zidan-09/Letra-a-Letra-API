package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.UUID;

public interface SaveInventory {
    void save(InventoryItem inventory, UUID userId);
}
