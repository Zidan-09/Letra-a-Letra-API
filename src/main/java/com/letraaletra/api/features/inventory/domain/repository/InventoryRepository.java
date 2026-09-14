package com.letraaletra.api.features.inventory.domain.repository;

import com.letraaletra.api.features.inventory.domain.UserItem;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository {
    List<UserItem> findItemsByOwner(UUID ownerId);
    void saveItem(UUID ownerId, UserItem item);
    void deleteItemsByOwner(UUID ownerId);
}
