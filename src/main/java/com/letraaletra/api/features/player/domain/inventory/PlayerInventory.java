package com.letraaletra.api.features.player.domain.inventory;

import com.letraaletra.api.features.game.domain.board.power.PowerType;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerInventory {
    private final LinkedHashMap<String, PowerType> inventory;

    private PlayerInventory(LinkedHashMap<String, PowerType> inventory) {
        this.inventory = Objects.requireNonNull(inventory);
    }

    public static PlayerInventory create() {
        return new PlayerInventory(new LinkedHashMap<>());
    }

    public static PlayerInventory restore(LinkedHashMap<String, PowerType> inventory) {
        return new PlayerInventory(new LinkedHashMap<>(Objects.requireNonNull(inventory)));
    }

    public Map<String, PowerType> getPowers() {
        return Map.copyOf(inventory);
    }

    public boolean hasFreezeDefense() {
        return inventory.values().stream().anyMatch(power -> power == PowerType.UNFREEZE || power == PowerType.IMMUNITY);
    }

    public void addToInventory(PowerType powerType) {
        if (inventory.size() == 5) return;

        String id = UUID.randomUUID().toString();

        inventory.put(id, powerType);
    }

    public void removeFromInventoryOrThrow(String id) {
        if (!inventory.containsKey(id)) {
            throw new InvalidPlayerActionException();
        }

        inventory.remove(id);
    }
}
