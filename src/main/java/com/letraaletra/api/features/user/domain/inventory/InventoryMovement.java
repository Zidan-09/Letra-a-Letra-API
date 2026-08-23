package com.letraaletra.api.features.user.domain.inventory;

import java.util.UUID;

public record InventoryMovement(
        UUID cosmeticId,
        InventoryChangeKind kind,
        Boolean equippedBefore,
        boolean equippedAfter
) {
}
