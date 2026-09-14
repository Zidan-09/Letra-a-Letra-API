package com.letraaletra.api.features.inventory.application.output;

import com.letraaletra.api.features.inventory.domain.InventoryMovement;

import java.util.List;

public record EquipItemOutput(
        List<InventoryMovement> movements
) {
}
