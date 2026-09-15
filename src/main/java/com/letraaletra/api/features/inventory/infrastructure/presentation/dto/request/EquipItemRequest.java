package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.items.domain.ItemContext;
import jakarta.validation.constraints.NotNull;

public record EquipItemRequest(
        @NotNull ItemContext context
) {
}
