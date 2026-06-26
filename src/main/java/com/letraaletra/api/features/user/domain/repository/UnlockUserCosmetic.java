package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.UUID;

public interface UnlockUserCosmetic {
    InventoryItem unlockCosmetic(String cosmeticId, UUID userId);
}
