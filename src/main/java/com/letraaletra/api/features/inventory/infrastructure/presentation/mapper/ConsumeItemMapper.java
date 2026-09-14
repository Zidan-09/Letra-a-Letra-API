package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.ConsumeItemRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.InventoryMovementsResponse;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemMovementResponse;

import java.util.UUID;

public class ConsumeItemMapper {
    public static ConsumeItemInput toInput(UUID ownerId, UUID itemId, ConsumeItemRequest request) {
        return new ConsumeItemInput(
                ownerId,
                itemId,
                request.quantity(),
                request.context()
        );
    }

    public static InventoryMovementsResponse toResponse(ConsumeItemOutput output) {
        return new InventoryMovementsResponse(
                output.movements().stream()
                        .map(ConsumeItemMapper::toMovementResponse)
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
