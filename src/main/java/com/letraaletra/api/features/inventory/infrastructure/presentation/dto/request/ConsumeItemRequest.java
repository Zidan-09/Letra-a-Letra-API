package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.items.domain.ItemContext;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConsumeItemRequest(
        @NotNull @Min(1) Integer quantity,
        @NotNull ItemContext context
) {
}
