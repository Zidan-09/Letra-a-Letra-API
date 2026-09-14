package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;

import java.util.List;
import java.util.UUID;

public final class InventoryPersistence {
    private InventoryPersistence() {
    }

    public static void save(InventoryRepository repository, UUID ownerId, Inventory inventory) {
        repository.deleteItemsByOwner(ownerId);

        List<UserItem> items = inventory.getItems();
        for (UserItem item : items) {
            repository.saveItem(ownerId, item);
        }
    }
}
