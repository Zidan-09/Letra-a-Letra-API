package com.letraaletra.api.features.user.domain.inventory;

import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;

import java.time.LocalDateTime;

public record InventoryItem(
        String cosmetic_id,
        String name,
        CosmeticTypes type,
        boolean equipped,
        LocalDateTime unlocked_at
) {
}
