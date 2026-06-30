package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryItem(
        UUID cosmeticId,
        String name,
        CosmeticTypes type,
        boolean equipped,
        LocalDateTime unlockedAt
) {
    public static InventoryItem create(
            UUID cosmeticId,
            String name,
            CosmeticTypes type
    ) {
        return new InventoryItem(
                cosmeticId,
                name,
                type,
                false,
                LocalDateTime.now()
        );
    }

    public static InventoryItem restore(
            UUID cosmeticId,
            String name,
            CosmeticTypes type,
            boolean equipped,
            LocalDateTime unlockedAt
    ) {
        return new InventoryItem(
                cosmeticId,
                name,
                type,
                equipped,
                unlockedAt
        );
    }
}
