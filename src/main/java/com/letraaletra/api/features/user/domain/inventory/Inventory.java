package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;

import java.util.List;

public class Inventory {
    private List<InventoryItem> inventory;

    public Inventory(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public List<InventoryItem> getItems() {
        return List.copyOf(inventory);
    }

    public void addToInventory(InventoryItem item) {
        if (item == null) {
            throw new CosmeticNotFoundException();
        }

        if (inventory.stream().anyMatch(cosmetic -> cosmetic.cosmetic_id().equals(item.cosmetic_id()))) {
            throw new InvalidCosmeticException();
        }

        inventory.add(item);
    }

    public void removeFromInventory(String cosmeticId) {
        InventoryItem itemToBeRemoved = inventory.stream()
                .filter(cosmetic -> cosmetic.cosmetic_id().equals(cosmeticId))
                .findFirst().orElseThrow();

        inventory.remove(itemToBeRemoved);

        if (itemToBeRemoved.equipped()) {
            InventoryItem newEquipped = inventory.stream()
                    .filter(cosmetic -> cosmetic.type().equals(itemToBeRemoved.type()))
                    .findFirst().orElseThrow();

            this.equipCosmetic(newEquipped.cosmetic_id());
        }
    }

    public void equipCosmetic(String cosmeticId) {
        InventoryItem targetItem = this.inventory.stream()
                .filter(item -> item.cosmetic_id().equals(cosmeticId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não possui este cosmético."));

        this.inventory = this.inventory.stream()
                .map(item -> {
                    if (item.type() == targetItem.type()) {
                        boolean isTarget = item.cosmetic_id().equals(cosmeticId);
                        return new InventoryItem(item.cosmetic_id(), item.name(), item.type(), isTarget, item.unlocked_at());
                    }

                    return item;
                })
                .toList();
    }
}
