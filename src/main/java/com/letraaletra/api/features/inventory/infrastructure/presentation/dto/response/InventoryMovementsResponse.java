package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import java.util.List;

public record InventoryMovementsResponse(
        List<ItemMovementResponse> movements
) {
}
