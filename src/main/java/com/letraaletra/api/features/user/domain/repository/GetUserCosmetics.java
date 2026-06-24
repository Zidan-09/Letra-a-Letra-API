package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;
import java.util.UUID;

public interface GetUserCosmetics {
    List<InventoryItem> getCosmetics(UUID userId);
}
