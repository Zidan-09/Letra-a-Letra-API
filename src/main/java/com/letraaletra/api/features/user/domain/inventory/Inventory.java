package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.user.domain.exception.InvalidUserCosmeticSelectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory {
    private List<InventoryItem> inventory;

    private Inventory(List<InventoryItem> inventory) {
        this.inventory = inventory;
    }

    public static Inventory create() {
        return new Inventory(
                new ArrayList<>()
        );
    }

    public static Inventory restore(List<InventoryItem> inventory) {
        return new Inventory(
                new ArrayList<>(inventory)
        );
    }

    public List<InventoryItem> getItems() {
        return List.copyOf(inventory);
    }

    public void unlock(Cosmetic cosmetic) {
        InventoryItem item = InventoryItem.create(
                cosmetic.getId(),
                cosmetic.getName(),
                cosmetic.getType()
        );

        addToInventory(item);
    }

    private void addToInventory(InventoryItem item) {
        if (item == null) {
            throw new CosmeticNotFoundException();
        }

        if (inventory.stream().anyMatch(cosmetic -> cosmetic.cosmeticId().equals(item.cosmeticId()))) {
            throw new InvalidCosmeticException();
        }

        inventory.add(item);
    }

    public void removeFromInventory(UUID cosmeticId) {
        InventoryItem itemToBeRemoved = inventory.stream()
                .filter(cosmetic -> cosmetic.cosmeticId().equals(cosmeticId))
                .findFirst()
                .orElseThrow(InvalidUserCosmeticSelectedException::new);

        inventory.remove(itemToBeRemoved);

        if (itemToBeRemoved.equipped()) {
            inventory.stream()
                    .filter(cosmetic -> cosmetic.type() == itemToBeRemoved.type())
                    .findFirst()
                    .ifPresent(cosmetic -> equipCosmetic(cosmetic.cosmeticId()));
        }
    }

    public void equipCosmetic(UUID cosmeticId) {
        InventoryItem targetItem = this.inventory.stream()
                .filter(item -> cosmeticId.equals(item.cosmeticId()))
                .findFirst()
                .orElseThrow(InvalidUserCosmeticSelectedException::new);

        this.inventory = this.inventory.stream()
                .map(item -> {
                    if (item.type() == targetItem.type()) {
                        boolean isTarget = cosmeticId.equals(item.cosmeticId());
                        return new InventoryItem(
                                item.cosmeticId(),
                                item.name(),
                                item.type(),
                                isTarget,
                                item.unlockedAt()
                        );
                    }

                    return item;
                })
                .toList();
    }
}
