package com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.inventory.domain.EffectType;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemKind;

import java.util.Set;
import java.util.UUID;

public record ItemDefinitionResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        Set<ItemContext> contexts,
        boolean stackable,
        Integer maxStack,
        boolean consumable,
        ItemEffectResponse effect,
        String assetPath,
        int version,
        boolean available
) {
    public record ItemEffectResponse(
            EffectType type,
            int magnitude,
            int durationMinutes
    ) {
    }
}
