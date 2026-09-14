package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.inventory.domain.InventoryChangeKind;

import java.util.UUID;

public record ItemMovementResponse(
        UUID itemId,
        InventoryChangeKind change,
        Boolean equippedBefore,
        boolean equippedAfter,
        int quantityBefore,
        int quantityAfter
) {
}
