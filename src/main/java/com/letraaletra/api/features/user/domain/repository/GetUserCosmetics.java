package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public interface GetUserCosmetics {
    List<InventoryItem> getCosmetics(String userId);
}
