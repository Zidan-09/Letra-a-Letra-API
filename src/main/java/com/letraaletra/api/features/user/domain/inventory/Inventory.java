package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.domain.exceptions.InvalidCosmeticException;
import com.letraaletra.api.features.user.domain.inventory.exception.InvalidUserCosmeticSelectedException;

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

    public List<InventoryMovement> unlock(Cosmetic cosmetic) {
        InventoryItem item = InventoryItem.create(
                cosmetic.getId(),
                cosmetic.getName(),
                cosmetic.getType()
        );

        addToInventory(item);

        return List.of(new InventoryMovement(
                item.cosmeticId(),
                InventoryChangeKind.ACQUIRED,
                null,
                false
        ));
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

    public List<InventoryMovement> removeFromInventory(UUID cosmeticId) {
        InventoryItem itemToBeRemoved = inventory.stream()
                .filter(cosmetic -> cosmetic.cosmeticId().equals(cosmeticId))
                .findFirst()
                .orElseThrow(InvalidUserCosmeticSelectedException::new);

        inventory.remove(itemToBeRemoved);

        List<InventoryMovement> movements = new ArrayList<>();

        movements.add(new InventoryMovement(
                itemToBeRemoved.cosmeticId(),
                InventoryChangeKind.REMOVED,
                itemToBeRemoved.equipped(),
                false
        ));

        if (itemToBeRemoved.equipped()) {
            inventory.stream()
                    .filter(cosmetic -> cosmetic.type() == itemToBeRemoved.type())
                    .findFirst()
                    .ifPresent(fallback -> movements.addAll(equipCosmetic(fallback.cosmeticId())));
        }

        return movements;
    }

    public List<InventoryMovement> equipCosmetic(UUID cosmeticId) {
        InventoryItem targetItem = this.inventory.stream()
                .filter(item -> cosmeticId.equals(item.cosmeticId()))
                .findFirst()
                .orElseThrow(InvalidUserCosmeticSelectedException::new);

        List<InventoryMovement> movements = new ArrayList<>();
        List<InventoryItem> updatedInventory = new ArrayList<>();

        for (InventoryItem item : this.inventory) {
            if (item.type() == targetItem.type()) {
                boolean isTarget = cosmeticId.equals(item.cosmeticId());
                boolean equippedAfter = isTarget;

                if (item.equipped() != equippedAfter) {
                    movements.add(new InventoryMovement(
                            item.cosmeticId(),
                            isTarget ? InventoryChangeKind.EQUIPPED : InventoryChangeKind.UNEQUIPPED,
                            item.equipped(),
                            equippedAfter
                    ));
                }

                updatedInventory.add(new InventoryItem(
                        item.cosmeticId(),
                        item.name(),
                        item.type(),
                        isTarget,
                        item.unlockedAt()
                ));
            } else {
                updatedInventory.add(item);
            }
        }

        this.inventory = updatedInventory;

        return movements;
    }
}
