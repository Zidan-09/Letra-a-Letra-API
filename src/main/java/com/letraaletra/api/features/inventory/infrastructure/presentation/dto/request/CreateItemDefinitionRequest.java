package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.inventory.domain.EffectType;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateItemDefinitionRequest(
        @NotBlank String name,
        @NotNull ItemKind kind,
        @NotNull ItemCategory category,
        Set<ItemContext> applicability,
        boolean stackable,
        @Min(1) Integer maxStack,
        boolean consumable,
        @Valid ItemEffectRequest effect,
        String assetPath
) {
    public record ItemEffectRequest(
            @NotNull EffectType type,
            @Min(1) int magnitude,
            @Min(1) int durationMinutes
    ) {
    }
}
