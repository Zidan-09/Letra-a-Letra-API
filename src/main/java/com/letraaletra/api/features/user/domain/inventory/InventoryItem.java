package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryItem(
        UUID cosmeticId,
        String name,
        CosmeticTypes type,
        boolean equipped,
        LocalDateTime unlockedAt,
        String assetPath
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
                LocalDateTime.now(),
                null
        );
    }

    public static InventoryItem restore(
            UUID cosmeticId,
            String name,
            CosmeticTypes type,
            boolean equipped,
            LocalDateTime unlockedAt
    ) {
        return restore(cosmeticId, name, type, equipped, unlockedAt, null);
    }

    public static InventoryItem restore(
            UUID cosmeticId,
            String name,
            CosmeticTypes type,
            boolean equipped,
            LocalDateTime unlockedAt,
            String assetPath
    ) {
        return new InventoryItem(
                cosmeticId,
                name,
                type,
                equipped,
                unlockedAt,
                assetPath
        );
    }
}
