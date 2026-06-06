package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

public interface UnlockUserCosmetic {
    InventoryItem unlockCosmetic(String cosmeticId, String userId);
}
