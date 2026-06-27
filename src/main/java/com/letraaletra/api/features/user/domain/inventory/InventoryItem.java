package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.time.LocalDateTime;

public record InventoryItem(
        String cosmeticId,
        String name,
        CosmeticTypes type,
        boolean equipped,
        LocalDateTime unlockedAt
) {
    public static InventoryItem create(
            String cosmeticId,
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
            String cosmeticId,
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
