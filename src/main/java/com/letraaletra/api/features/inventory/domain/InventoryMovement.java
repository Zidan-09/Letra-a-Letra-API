package com.letraaletra.api.features.inventory.domain;

import java.util.UUID;

public record InventoryMovement(
        UUID itemId,
        InventoryChangeKind kind,
        Boolean equippedBefore,
        boolean equippedAfter,
        int quantityBefore,
        int quantityAfter
) {
}
