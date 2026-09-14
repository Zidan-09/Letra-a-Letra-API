package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.EquipItemInput;
import com.letraaletra.api.features.inventory.application.output.EquipItemOutput;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.EquipItemRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.InventoryMovementsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemMovementResponse;

import java.util.UUID;

public class EquipItemMapper {
    public static EquipItemInput toInput(UUID ownerId, UUID itemId, EquipItemRequest request) {
        return new EquipItemInput(
                ownerId,
                itemId,
                request.context()
        );
    }

    public static InventoryMovementsResponse toResponse(EquipItemOutput output) {
        return new InventoryMovementsResponse(
                output.movements().stream()
                        .map(EquipItemMapper::toMovementResponse)
                        .toList()
        );
    }

    private static ItemMovementResponse toMovementResponse(InventoryMovement movement) {
        return new ItemMovementResponse(
                movement.itemId(),
                movement.kind(),
                movement.equippedBefore(),
                movement.equippedAfter(),
                movement.quantityBefore(),
                movement.quantityAfter()
        );
    }
}
