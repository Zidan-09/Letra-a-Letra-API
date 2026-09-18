package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.items.domain.EquippableContext;
import jakarta.validation.constraints.NotNull;

public record EquipItemRequest(
        @NotNull EquippableContext context
) {
}
